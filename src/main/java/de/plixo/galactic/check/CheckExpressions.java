package de.plixo.galactic.check;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.lowering.Tree;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;

import java.lang.reflect.Modifier;

/**
 * Check stage is the final Stage for the Expressions, to test if every expression is valid.
 */
public class CheckExpressions implements Tree<Context, Integer> {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Check stage");
    }

    @Override
    public Expression parseCastExpression(CastExpression expression, Context context,
                                          Integer unused) {
        var parsed = parse(expression.object(), context, 0);
        var type = expression.type();
        if (!(type instanceof Class)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "Cant cast to primitive type or array");
        }
        var parsedType = parsed.getType(context);
        if (!Type.isAssignableFrom(type, parsedType, context) &&
                !Type.isAssignableFrom(parsedType, type, context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "Cant cast across inheritance, this cast will always fail");
        }
        return new CastExpression(expression.region(), parsed, type);
    }

    @Override
    public Expression parseCastCheckExpression(CastCheckExpression expression, Context context,
                                               Integer unused) {
        var parsed = parse(expression.object(), context, 0);
        var type = expression.type();
        if (!(type instanceof Class)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "Cant cast to primitive type or array");
        }
        return new CastCheckExpression(expression.region(), parsed, type);
    }

    @Override
    public Expression parsePutFieldExpression(PutFieldExpression expression, Context context,
                                              Integer unused) {
        parse(expression.object(), context, 0);
        var value = parse(expression.value(), context, 0);

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
    public Expression parsePutStaticExpression(PutStaticFieldExpression expression, Context context,
                                               Integer unused) {
        var value = parse(expression.value(), context, 0);
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
    public Expression parseLocalVariableAssign(LocalVariableAssign expression, Context context,
                                               Integer unused) {
        var variable = expression.variable();
        assert variable != null;
        if (variable.isFinal()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant reassign final variable");
        }
        var parsed = parse(expression.expression(), context, 0);
        if (!Type.isAssignableFrom(variable.getType(), parsed.getType(context), context)) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "variable type does not match expression type");
        }
        return expression;
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context,
                                            Integer unused) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context,
                                            Integer unused) {
        return expression;
    }

    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context,
                                             Integer unused) {
        return expression;
    }

    @Override
    public Expression parseObjectFieldExpression(FieldExpression expression, Context context,
                                                 Integer unused) {
        parse(expression.object(), context, 0);
        if (!expression.field().isPublic()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant access non public field");
        }

        return expression;
    }

    @Override
    public Expression parseMethodCallExpression(MethodCallExpression expression, Context context,
                                                Integer unused) {
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
        found.forEach(ref -> parse(ref, context, 0));
        if (expression.source() instanceof GetMethodExpression getMethodExpression) {
            parse(getMethodExpression.object(), context, 0);
        }

        return expression;
    }


    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context,
                                            Integer unused) {
        var condition = parse(expression.condition(), context, 0);
        parse(expression.then(), context, 0);

        var found = expression.condition().getType(context);
        if (!Type.isAssignableFrom(PrimitiveType.BOOLEAN, found, context)) {
            throw new FlairCheckException(condition.region(), FlairKind.TYPE_MISMATCH,
                    "condition is not boolean");
        }
        if (expression.elseExpression() != null) {
            parse(expression.elseExpression(), context, 0);
        }

        return expression;
    }

    @Override
    public Expression parseBlockExpression(BlockExpression expression, Context context,
                                           Integer unused) {
        expression.expressions().forEach(ref -> parse(ref, context, 0));
        return expression;
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression expression, Context context,
                                            Integer unused) {
        var parsedExpression = parse(expression.expression(), context, 0);
        if (expression.variable() == null) {
            throw new FlairException("variable is null");
        }
        var name = expression.variable().name();
        if (!CheckProject.isAllowedTopLevelName(name)) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "variable name is not allowed");
        }

        var found = parsedExpression.getType(context);
        if (expression.hint() != null) {
            var expected = expression.hint();
            if (!Type.isAssignableFrom(expected, found, context)) {
                throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                        STR."Hint does not match expression, expected \{expected} found \{found}");
            }
        }
        if (found.isVoid()) {
            throw new FlairCheckException(expression.region(), FlairKind.TYPE_MISMATCH,
                    "variable cant be void");
        }

        return expression;
    }

    @Override
    public Expression parseVarExpression(VarExpression expression, Context context,
                                         Integer unused) {
        return expression;
    }

    @Override
    public Expression parseThisExpression(ThisExpression thisExpression, Context context,
                                          Integer hint) {
        if (!Type.isSame(thisExpression.type(), context.thisContext())) {
            throw new FlairCheckException(thisExpression.region(), FlairKind.TYPE_MISMATCH,
                    "this expression type does not match 'this' context");
        }
        return thisExpression;
    }

    @Override
    public Expression parseStaticFieldExpression(StaticFieldExpression expression, Context context,
                                                 Integer unused) {
        if (!expression.field().isPublic()) {
            throw new FlairCheckException(expression.region(), FlairKind.SECURITY,
                    "cant access non public fields");
        }
        return expression;
    }

    @Override
    public Expression parseInstanceCreationExpression(InstanceCreationExpression expression,
                                                      Context context, Integer unused) {

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
        found.forEach(ref -> parse(ref, context, 0));

        return expression;
    }
}