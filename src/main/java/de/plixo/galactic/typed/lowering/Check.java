package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;

import java.lang.reflect.Modifier;

public class Check implements Tree<Context> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Check stage");
    }

    @Override
    public Expression parseCastExpression(CastExpression expression, Context context) {
        var parsed = parse(expression.object(), context);
        var type = expression.type();
        if (!(type instanceof Class)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "Cant cast to primitive type or array");
        }
        if (!Type.isAssignableFrom(parsed.getType(context), type, context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "Cant cast across inheritance hierarchy");
        }
        return new CastExpression(expression.region(), parsed, type);
    }

    @Override
    public Expression parsePutFieldExpression(PutFieldExpression expression, Context context) {
        parse(expression.object(), context);
        var value = parse(expression.value(), context);

        var field = expression.field();
        if (!field.isPublic() || field.isFinal()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant reassign non public/final field");
        }

        if (!Type.isAssignableFrom(field.type(), value.getType(context), context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "field type does not match expression type");
        }

        return expression;
    }

    @Override
    public Expression parsePutStaticExpression(PutStaticFieldExpression expression,
                                               Context context) {
        var value = parse(expression.value(), context);
        var field = expression.field();
        if (!field.isPublic() || field.isFinal()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant reassign non public/final field");
        }

        if (!Type.isAssignableFrom(field.type(), value.getType(context), context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "field type does not match expression type");
        }
        return expression;
    }

    @Override
    public Expression parseLocalVariableAssign(LocalVariableAssign expression, Context context) {

        var parsed = parse(expression.expression(), context);
        var variable = expression.variable();
        assert variable != null;
        if (!Type.isAssignableFrom(variable.getType(), parsed.getType(context), context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "variable type does not match expression type");
        }
        return expression;
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
    public Expression parseObjectFieldExpression(FieldExpression expression, Context context) {
        parse(expression.object(), context);
        if (!expression.field().isPublic()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant access non public methods");
        }

        return expression;
    }

    @Override
    public Expression parseMethodCallExpression(MethodCallExpression expression, Context context) {
        var method = expression.method();
        var region = expression.region();
        if (!method.isPublic()) {
            throw new FlairCheckException(region, FlairKind.SECURITY,
                    "cant access non public methods");
        }
        var expected = method.arguments();
        var found = expression.arguments();

        if (expected.size() != found.size()) {
            throw new FlairCheckException(region, FlairKind.SIGNATURE,
                    "method call arguments dont match");
        }
        for (int i = 0; i < expected.size(); i++) {
            var expectedType = expected.get(i);
            var foundType = found.get(i).getType(context);
            if (!Type.isAssignableFrom(expectedType, foundType, context)) {
                throw new FlairCheckException(region, FlairKind.SIGNATURE,
                        "method call arguments dont match");
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

        var found = expression.condition().getType(context);
        if (!Type.isAssignableFrom(PrimitiveType.BOOLEAN, found, context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "condition is not boolean, its " + found);
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
            var found = expression.expression().getType(context);
            if (!Type.isAssignableFrom(expected, found, context)) {
                throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                        "hint does not match expression, expected " + expected + " found " + found);
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
        if (!expression.field().isPublic()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant access non public fields");
        }
        return expression;
    }

    @Override
    public Expression parseInstanceCreationExpression(InstanceCreationExpression expression,
                                                      Context context) {

        var constructor = expression.constructor();
        var region = expression.region();
        if (!Modifier.isPublic(constructor.modifier())) {
            throw new FlairCheckException(region, FlairKind.SECURITY,
                    "cant access non public constructor");
        }
        var expected = constructor.arguments();
        var found = expression.expressions();

        if (expected.size() != found.size()) {
            throw new FlairCheckException(region, FlairKind.SIGNATURE,
                    "constructor call arguments dont match");
        }
        for (int i = 0; i < expected.size(); i++) {
            var expectedType = expected.get(i);
            var foundType = found.get(i).getType(context);
            if (!Type.isAssignableFrom(expectedType, foundType, context)) {
                throw new FlairCheckException(region, FlairKind.SIGNATURE,
                        "constructor call arguments dont match");
            }
        }
        found.forEach(ref -> parse(ref, context));

        return expression;
    }
}