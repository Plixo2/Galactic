package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Signature;
import de.plixo.galactic.types.VoidType;

import java.util.Objects;

import static de.plixo.galactic.exception.FlairKind.*;

public class Infer implements Tree<Context> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Infer stage");
    }

    @Override
    public Expression parseAssign(AssignExpression expression, Context context) {
        var left = parse(expression.left(), context);
        var value = parse(expression.right(), context);
        if (left instanceof FieldExpression fieldExpression) {
            if (fieldExpression.field().isStatic()) {
                throw new FlairCheckException(left.region(), FORMAT,
                        STR."Cannot access a static field \{fieldExpression.field()
                                .name()} on an object");
            }
            return new PutFieldExpression(expression.region(), fieldExpression.field(),
                    fieldExpression.object(), value);
        } else {
            throw new FlairCheckException(left.region(), FORMAT,
                    STR."Cannot assign to \{left.getClass().getSimpleName()}");
        }
    }

    @Override
    public Expression parsePutStaticExpression(PutStaticFieldExpression expression,
                                               Context context) {
        return new PutStaticFieldExpression(expression.region(), expression.field(),
                expression.value());
    }

    @Override
    public Expression parseLocalVariableAssign(LocalVariableAssign expression,
                                               Context context) {
        var parsed = parse(expression.expression(), context);
        return new LocalVariableAssign(expression.region(), expression.variable(), parsed);
    }

    @Override
    public Expression parseStaticMethodExpression(StaticMethodExpression expression,
                                                  Context context) {
        return expression;
    }

    @Override
    public Expression parseStaticFieldExpression(StaticFieldExpression expression,
                                                 Context context) {
        return expression;
    }

    @Override
    public Expression parseStellaClassConstructExpression(StellaClassConstructExpression expression,
                                                          Context context) {
        var region = expression.region();
        var parsedArguments =
                expression.arguments().stream().map(ref -> parse(ref, context)).toList();

        var constructType = expression.constructType();
        var constructors = constructType.getMethods("<init>", context)
                .filter(ref -> ref.isPublic() && !ref.isStatic());
        constructors = constructors.filter(
                ref -> ref.owner().equals(new MethodOwner.ClassOwner(constructType)));
        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();
        var signature = new Signature(new VoidType(), types);
        var bestMatch = constructors.findBestMatch(signature, context);

        if (bestMatch == null) {
            throw new FlairCheckException(region, SIGNATURE,
                    STR."Constructor not found on \{constructType.name()}");
        }
        return new InstanceCreationExpression(region, bestMatch, constructType, parsedArguments);
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, Context context) {
        var parsed = expression.expressions().stream().map(ref -> parse(ref, context)).toList();
        return new BlockExpression(expression.region(), parsed);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context) {
        var parsedCondition = parse(expression.condition(), context);

        var parsedThen = parse(expression.then(), context);
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            parsedElse = parse(expression.elseExpression(), context);
        }
        return new BranchExpression(expression.region(), parsedCondition, parsedThen, parsedElse);
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, Context context) {
        var parsed = parse(expression.object(), context);
        var parsedArguments =
                expression.arguments().stream().map(ref -> parse(ref, context)).toList();

        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();
        var signature = new Signature(null, types);
        if (parsed instanceof GetMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(signature, context);
            if (bestMatch == null) {
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        STR."Method type types \{types} not found");
            }
            return new MethodCallExpression(parsed.region(), methodExpression, bestMatch,
                    methodExpression.object().getType(context), parsedArguments);
        } else if (parsed instanceof StaticMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(signature, context);
            if (bestMatch == null) {
                throw new FlairCheckException(parsed.region(), SIGNATURE,
                        STR."Method types \{types} not found \{methodExpression.methods()}");
            }
            return new MethodCallExpression(parsed.region(), methodExpression, bestMatch,
                    new VoidType(), parsedArguments);
        } else {
            throw new FlairCheckException(parsed.region(), UNEXPECTED_TYPE,
                    "Can only call methods");
        }

    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context) {
        return expression;
    }
    @Override
    public Expression parseVarExpression(VarExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseCastExpression(CastExpression expression, Context context) {
        var parsed = parse(expression.object(), context);
        return new CastExpression(expression.region(), parsed, expression.type());
    }
    @Override
    public Expression parseCastCheckExpression(CastCheckExpression expression, Context context) {
        var parsed = parse(expression.object(), context);
        return new CastCheckExpression(expression.region(), parsed, expression.type());
    }


    @Override
    public Expression parseVarDefExpression(VarDefExpression expression, Context context) {
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
    public Expression parseDotNotation(DotNotation expression, Context context) {
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
