package de.plixo.galactic.tir.stages;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.VoidType;

import java.util.Objects;

import static de.plixo.galactic.exception.FlairKind.*;

public class Infer implements Tree<TypeContext> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Infer stage");
    }

    @Override
    public Expression parseAssign(AssignExpression expression, TypeContext context) {
        var left = parse(expression.left(), context);
        var value = parse(expression.right(), context);
        if (left instanceof FieldExpression fieldExpression) {
            if (fieldExpression.field().isStatic()) {
                throw new FlairCheckException(left.region(), FORMAT,
                        STR."Cannot access a static field \{fieldExpression.field().name()} on an object");
            }
            return new PutFieldExpression(expression.region(), fieldExpression.field(), fieldExpression.object(), value);
        } else {
            throw new FlairCheckException(left.region(), FORMAT,
                    STR."Cannot assign to \{left.getClass().getSimpleName()}");
        }
    }

    @Override
    public Expression parsePutStaticExpression(PutStaticFieldExpression expression,
                                               TypeContext context) {
        return new PutStaticFieldExpression(expression.region(), expression.field(),
                expression.value());
    }

    @Override
    public Expression parseLocalVariableAssign(LocalVariableAssign expression,
                                               TypeContext context) {
        var parsed = parse(expression.expression(), context);
        return new LocalVariableAssign(expression.region(), expression.variable(), parsed);
    }

    @Override
    public Expression parseStaticMethodExpression(StaticMethodExpression expression,
                                                  TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseStaticFieldExpression(StaticFieldExpression expression,
                                                 TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseStellaClassConstructExpression(StellaClassConstructExpression expression,
                                                          TypeContext context) {
        var region = expression.region();
        var parsedArguments =
                expression.arguments().stream().map(ref -> parse(ref, context)).toList();

        var constructType = expression.constructType();
        var constructors = constructType.getMethods("<init>", context);
        constructors = constructors.filter(
                ref -> ref.owner().equals(new MethodOwner.ClassOwner(constructType)));
        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();
        var bestMatch = constructors.findBestMatch(types, context);

        if (bestMatch == null) {
            throw new FlairCheckException(region, SIGNATURE,
                    types + " not found on " + constructType.name());
        }

        if (!bestMatch.isCallable(types, context)) {
            throw new FlairCheckException(region, SIGNATURE, "cant call " + bestMatch);
        }
        return new InstanceCreationExpression(region, bestMatch, constructType, parsedArguments);
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, TypeContext context) {
        var parsed = expression.expressions().stream().map(ref -> parse(ref, context)).toList();
        return new BlockExpression(expression.region(), parsed);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, TypeContext context) {
        var parsedCondition = parse(expression.condition(), context);

        var parsedThen = parse(expression.then(), context);
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            parsedElse = parse(expression.elseExpression(), context);
        }
        return new BranchExpression(expression.region(), parsedCondition, parsedThen, parsedElse);
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, TypeContext context) {
        var parsed = parse(expression.object(), context);
        var parsedArguments =
                expression.arguments().stream().map(ref -> parse(ref, context)).toList();

        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();

        if (parsed instanceof GetMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        "Method type types " + types + " not found");
            }
            return new MethodCallExpression(parsed.region(), methodExpression, bestMatch,
                    methodExpression.object().getType(context), parsedArguments);
        } else if (parsed instanceof StaticMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        "Method types " + types + " not found " + methodExpression.methods());
            }
            return new MethodCallExpression(parsed.region(), methodExpression, bestMatch,
                    new VoidType(), parsedArguments);
        } else {
            throw new FlairCheckException(parsed.region(), UNEXPECTED_TYPE,
                    "Can only call methods");
        }

    }

    @Override
    public Expression parseStringExpression(StringExpression expression, TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression expression, TypeContext context) {
        var parsed = parse(expression.expression(), context);

        var variable = Objects.requireNonNull(expression.variable());
        if (expression.hint() != null) {
            variable.setType(expression.hint());
        } else {
            variable.setType(parsed.getType(context));
        }


        return new VarDefExpression(expression.region(), expression.name(), expression.hint(),
                parsed, variable);
    }

    @Override
    public Expression parseVarExpression(VarExpression expression, TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, TypeContext context) {
        var parsed = parse(expression.object(), context);

        var id = expression.id();
        if (parsed.getType(context) instanceof Class aClass) {
            return aClass.getDotNotation(expression.region(), parsed, id, context);

        } else {
            throw new FlairCheckException(expression.region(), UNEXPECTED_TYPE,
                    "only fields in classes are supported");
        }
    }
}
