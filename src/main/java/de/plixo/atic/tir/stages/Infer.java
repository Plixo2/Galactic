package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.types.Class;

import java.util.Objects;

public class Infer implements Tree<TypeContext> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for Infer stage");
    }

    @Override
    public Expression parseLocalVariableAssign(LocalVariableAssign expression,
                                               TypeContext context) {
        var parsed = parse(expression.expression(), context);
        return new LocalVariableAssign(expression.variable(), parsed);
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
    public Expression parseAticClassConstructExpression(AticClassConstructExpression expression,
                                                        TypeContext context) {
        var parsedArguments = expression.arguments().stream().map(ref -> {
            var parsedRef = parse(ref, context);
            return parsedRef;
        }).toList();

        var constructors = expression.constructType().getMethods("<init>", context);
        var bestMatch = constructors.findBestMatch(
                parsedArguments.stream().map(ref -> ref.getType(context)).toList(), context);
        if (bestMatch == null) {
            throw new NullPointerException("cant find fitting constructor");
        }
        return new InstanceCreationExpression(bestMatch, expression.constructType(),parsedArguments);
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, TypeContext context) {
        var parsed = expression.expressions().stream().map(ref -> parse(ref, context)).toList();
        return new BlockExpression(parsed);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, TypeContext context) {
        var parsedCondition = parse(expression.condition(), context);

        var parsedThen = parse(expression.then(), context);
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            parsedElse = parse(expression.elseExpression(), context);
        }
        return new BranchExpression(parsedCondition, parsedThen, parsedElse);
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, TypeContext context) {
        var parsed = parse(expression.object(), context);

        var parsedArguments = expression.arguments().stream().map(ref -> parse(ref, context)).toList();

        var types = parsedArguments.stream().map(ref -> ref.getType(context)).toList();

        if (parsed instanceof GetMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new NullPointerException("Method type types " + types + " not found");
            }
            return new MethodCallExpression(methodExpression, bestMatch, parsedArguments);
        } else if (parsed instanceof StaticMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new NullPointerException("Method type types " + types + " not found " + methodExpression.methods());
            }
            return new MethodCallExpression(methodExpression, bestMatch, parsedArguments);
        } else {
            throw new NullPointerException("can only call methods");
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


        return new VarDefExpression(expression.name(), expression.hint(), parsed, variable);
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
            var field = aClass.getField(id, context);
            if (field != null) {
                return new FieldExpression(parsed, field);
            }
            var methods = aClass.getMethods(id, context);
            if (!methods.isEmpty()) {
                return new GetMethodExpression(parsed, methods);
            }
            throw new NullPointerException(
                    "Field or Methods " + id + " not found in class " + aClass.path());

        } else {
            throw new NullPointerException("only fields in classes are supported " + expression);
        }
    }
}
