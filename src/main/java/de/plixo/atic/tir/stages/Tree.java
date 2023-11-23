package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.*;

import java.util.Objects;

public interface Tree {

    Expression defaultBehavior(Expression expression);

    default Expression parse(Expression expression, Context context) {
        return Objects.requireNonNull(switch (expression) {
            case ArrayConstructExpression arrayConstructExpression ->
                    parseArrayConstructExpression(arrayConstructExpression, context);
            case BlockExpression blockExpression -> parseBlockExpression(blockExpression, context);
            case BooleanExpression booleanExpression ->
                    parseBooleanExpression(booleanExpression, context);
            case BranchExpression branchExpression ->
                    parseBranchExpression(branchExpression, context);
            case CallNotation callNotation -> parseCallNotation(callNotation, context);
            case ClassExpression classExpression -> parseClassExpression(classExpression, context);
            case ConstructExpression constructExpression ->
                    parseConstructExpression(constructExpression, context);
            case DotNotation dotNotation -> parseDotNotation(dotNotation, context);
            case MethodInvokeExpression methodInvokeExpression ->
                    parseMethodInvokeExpression(methodInvokeExpression, context);
            case NumberExpression numberExpression ->
                    parseNumberExpression(numberExpression, context);
            case ObjectFieldExpression objectFieldExpression ->
                    parseObjectFieldExpression(objectFieldExpression, context);
            case ObjectMethodExpression objectMethodExpression ->
                    parseObjectMethodExpression(objectMethodExpression, context);
            case Path path -> parsePath(path, context);
            case StaticFieldExpression staticFieldExpression ->
                    parseStaticFieldExpression(staticFieldExpression, context);
            case StaticMethodExpression staticMethodExpression ->
                    parseStaticMethodExpression(staticMethodExpression, context);
            case StringExpression stringExpression ->
                    parseStringExpression(stringExpression, context);
            case SymbolExpression symbolExpression ->
                    parseSymbolExpression(symbolExpression, context);
            case UnaryExpression unaryExpression -> parseUnaryExpression(unaryExpression, context);
            case VarDefExpression varDefExpression ->
                    parseVarDefExpression(varDefExpression, context);
            case VarExpression varExpression -> parseVarExpression(varExpression, context);
        }, expression.getClass().getSimpleName());
    }
    default Expression parseVarExpression(
            VarExpression expression, Context context) {
        return defaultBehavior(expression);
    }
    default Expression parseArrayConstructExpression(
            ArrayConstructExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseBlockExpression(BlockExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseBooleanExpression(BooleanExpression expression,
                                              Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseBranchExpression(BranchExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseCallNotation(CallNotation expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseClassExpression(ClassExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseConstructExpression(ConstructExpression expression,
                                                Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseDotNotation(DotNotation expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseMethodInvokeExpression(MethodInvokeExpression expression,
                                                   Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseNumberExpression(NumberExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectFieldExpression(ObjectFieldExpression expression,
                                                  Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectMethodExpression(ObjectMethodExpression expression,
                                                   Context context) {
        return defaultBehavior(expression);
    }

    default Expression parsePath(Path expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticFieldExpression(StaticFieldExpression expression,
                                                  Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticMethodExpression(StaticMethodExpression expression,
                                                   Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseStringExpression(StringExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseSymbolExpression(SymbolExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseUnaryExpression(UnaryExpression expression, Context context) {
        return defaultBehavior(expression);
    }

    default Expression parseVarDefExpression(
            VarDefExpression expression, Context context) {
        return defaultBehavior(expression);
    }

}
