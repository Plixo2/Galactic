package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.types.AClass;

import java.util.Objects;

public class Infer implements Tree<TypeContext> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for Infer stage");
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
            context.pushScope();
            var parsedRef = parse(ref, context);
            context.popScope();
            return parsedRef;
        }).toList();

        var constructors = expression.constructType().getMethods("<init>", context);
        var bestMatch = constructors.findBestMatch(
                parsedArguments.stream().map(Expression::getType).toList(), context);
        if (bestMatch == null) {
            throw new NullPointerException("cant find fitting constructor");
        }
        return new InstanceCreationExpression(bestMatch, expression.constructType(),parsedArguments);
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, TypeContext context) {
        context.pushScope();
        var parsed = expression.expressions().stream().map(ref -> parse(ref, context)).toList();
        context.popScope();
        return new BlockExpression(parsed);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, TypeContext context) {
        context.pushScope();
        var parsedCondition = parse(expression.condition(), context);
        context.popScope();

        context.pushScope(new Scope(context.scope()));
        var parsedThen = parse(expression.then(), context);
        context.popScope();
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            context.pushScope(new Scope(context.scope()));
            parsedElse = parse(expression.elseExpression(), context);
            context.popScope();
        }
        return new BranchExpression(parsedCondition, parsedThen, parsedElse);
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, TypeContext context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var parsedArguments = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parsedRef = parse(ref, context);
            context.popScope();
            return parsedRef;
        }).toList();

        var types = parsedArguments.stream().map(Expression::getType).toList();

        if (parsed instanceof GetMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new NullPointerException("Method type types " + types + " not found");
            }
            return new MethodCallExpression(methodExpression, bestMatch, parsedArguments);
        } else if (parsed instanceof StaticMethodExpression methodExpression) {
            var bestMatch = methodExpression.methods().findBestMatch(types, context);
            if (bestMatch == null) {
                throw new NullPointerException("Method type types " + types + " not found");
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
        context.pushScope();
        var parsed = parse(expression.expression(), context);
        context.popScope();


        var variable = Objects.requireNonNull(expression.variable());
        if (expression.hint() != null) {
            variable.setType(expression.hint());
        } else {
            variable.setType(parsed.getType());
        }
        context.scope().addVariable(variable);


        return new VarDefExpression(expression.name(), expression.hint(), parsed, variable);
    }

    @Override
    public Expression parseVarExpression(VarExpression expression, TypeContext context) {
        return expression;
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, TypeContext context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var id = expression.id();
        if (parsed.getType() instanceof AClass aClass) {
            var field = aClass.getField(id, context);
            if (field != null) {
                return new GetFieldExpression(parsed, field);
            }
            var methods = aClass.getMethods(id, context);
            if (!methods.isEmpty()) {
                return new GetMethodExpression(parsed, methods);
            }
            throw new NullPointerException(
                    "Field or Methods " + id + " not found in class " + aClass.path());

        } else {
            throw new NullPointerException("only fields in classes are supported");
        }
    }
}
