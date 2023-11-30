package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;

import java.lang.reflect.Modifier;

public class Check implements Tree<Context> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for CheckTypes stage");
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseObjectFieldExpression(GetFieldExpression expression, Context context) {
        parse(expression.object(), context);
        var modifier = expression.field().modifier();
        if (!Modifier.isPublic(modifier)) {
            throw new NullPointerException("cant access non public fields");
        }

        return expression;
    }

    @Override
    public Expression parseMethodCallExpression(MethodCallExpression expression, Context context) {
        var method = expression.method();
        var modifier = method.modifier();
        if (!Modifier.isPublic(modifier)) {
            throw new NullPointerException("cant access non public methods");
        }
        var expected = method.arguments();
        var found = expression.arguments();

        if (expected.size() != found.size()) {
            throw new NullPointerException("method call arguments dont match");
        }
        for (int i = 0; i < expected.size(); i++) {
            var expectedType = expected.get(i);
            var foundType = found.get(i).getType();
            if (!AType.isAssignableFrom(expectedType, foundType, context)) {
                throw new NullPointerException("method call arguments dont match");
            }
        }
        found.forEach(ref -> parse(ref, context));
        if (expression.source() instanceof GetMethodExpression getMethodExpression) {
            parse(getMethodExpression.object(), context);
        }

        return expression;
    }


    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context) {
        parse(expression.condition(), context);
        parse(expression.then(), context);

        var found = expression.condition().getType();
        if (!AType.isAssignableFrom(APrimitive.BOOLEAN, found, context)) {
            throw new NullPointerException("condition is not boolean, its " + found);
        }
        if (expression.elseExpression() != null) {
            parse(expression.elseExpression(), context);
        }

        return expression;
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, Context context) {
        expression.expressions().forEach(ref -> parse(ref, context));
        return expression;
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression expression, Context context) {
        parse(expression.expression(), context);

        if (expression.hint() != null) {
            var expected = expression.hint();
            var found = expression.expression().getType();
            if (!AType.isAssignableFrom(expected, found, context)) {
                throw new NullPointerException("hint does not match expression, expected " + expected + " found " + found);
            }
        }

        return expression;
    }

    @Override
    public Expression parseVarExpression(VarExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseStaticFieldExpression(StaticFieldExpression expression,
                                                 Context context) {
        var modifier = expression.field().modifier();
        if (!Modifier.isPublic(modifier)) {
            throw new NullPointerException("cant access non public fields");
        }
        return expression;
    }

    @Override
    public Expression parseInstanceCreationExpression(InstanceCreationExpression expression,
                                                      Context context) {

        var constructor = expression.constructor();
        if (!Modifier.isPublic(constructor.modifier())) {
            throw new NullPointerException("cant access non public constructor");
        }
        var expected = constructor.arguments();
        var found = expression.expressions();

        if (expected.size() != found.size()) {
            throw new NullPointerException("constructor call arguments dont match");
        }
        for (int i = 0; i < expected.size(); i++) {
            var expectedType = expected.get(i);
            var foundType = found.get(i).getType();
            if (!AType.isAssignableFrom(expectedType, foundType, context)) {
                throw new NullPointerException("constructor call arguments dont match");
            }
        }
        found.forEach(ref -> parse(ref, context));

        return expression;
    }
}