package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.*;

import java.util.Objects;

public interface Tree<C extends Context> {

    Expression defaultBehavior(Expression expression);

    default Expression parse(Expression expression, C context) {
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
            case MethodCallExpression methodCallExpression ->
                    parseMethodCallExpression(methodCallExpression, context);
            case NumberExpression numberExpression ->
                    parseNumberExpression(numberExpression, context);
            case FieldExpression fieldExpression ->
                    parseObjectFieldExpression(fieldExpression, context);
            case GetMethodExpression methodExpression ->
                    parseObjectMethodExpression(methodExpression, context);
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
            case AticClassConstructExpression aticClassConstructExpression ->
                    parseAticClassConstructExpression(aticClassConstructExpression, context);
            case StaticClassExpression staticClassExpression ->
                    parseAticClassExpression(staticClassExpression, context);
            case AticPackageExpression aticPackageExpression ->
                    parseAticPackageExpression(aticPackageExpression, context);
            case UnitExpression unitExpression -> parseUnitExpression(unitExpression, context);
            case InstanceCreationExpression instanceCreationExpression ->
                    parseInstanceCreationExpression(instanceCreationExpression, context);
            case LocalVariableAssign localVariableAssign ->
                    parseLocalVariableAssign(localVariableAssign, context);
            case AssignExpression assignExpression ->
                    parseAssign(assignExpression, context);
        }, expression.getClass().getSimpleName());
    }
    default Expression parseAssign(AssignExpression expression,
                                                C context) {
        return defaultBehavior(expression);
    }

    default Expression parseLocalVariableAssign(LocalVariableAssign expression,
                                                       C context) {
        return defaultBehavior(expression);
    }

    default Expression parseInstanceCreationExpression(InstanceCreationExpression expression,
                                                       C context) {
        return defaultBehavior(expression);
    }

    default Expression parseUnitExpression(UnitExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseAticPackageExpression(AticPackageExpression expression,
                                                  C context) {
        return defaultBehavior(expression);
    }

    default Expression parseAticClassExpression(StaticClassExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseAticClassConstructExpression(AticClassConstructExpression expression,
                                                         C context) {
        return defaultBehavior(expression);
    }

    default Expression parseVarExpression(VarExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseArrayConstructExpression(ArrayConstructExpression expression,
                                                     C context) {
        return defaultBehavior(expression);
    }

    default Expression parseBlockExpression(BlockExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseBooleanExpression(BooleanExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseBranchExpression(BranchExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseCallNotation(CallNotation expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseClassExpression(ClassExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseConstructExpression(ConstructExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseDotNotation(DotNotation expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseMethodCallExpression(MethodCallExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseNumberExpression(NumberExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectFieldExpression(FieldExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectMethodExpression(GetMethodExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parsePath(Path expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticFieldExpression(StaticFieldExpression expression,
                                                  C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticMethodExpression(StaticMethodExpression expression,
                                                   C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStringExpression(StringExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseSymbolExpression(SymbolExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseUnaryExpression(UnaryExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseVarDefExpression(VarDefExpression expression, C context) {
        return defaultBehavior(expression);
    }

}
