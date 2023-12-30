package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;

import java.util.Objects;

public interface Tree<C extends Context> {

    Expression defaultBehavior(Expression expression);

    default Expression parse(Expression expression, C context) {
        return Objects.requireNonNull(switch (expression) {
            case BlockExpression blockExpression -> parseBlockExpression(blockExpression, context);
            case BooleanExpression booleanExpression ->
                    parseBooleanExpression(booleanExpression, context);
            case BranchExpression branchExpression ->
                    parseBranchExpression(branchExpression, context);
            case CallNotation callNotation -> parseCallNotation(callNotation, context);
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
            case StaticFieldExpression staticFieldExpression ->
                    parseStaticFieldExpression(staticFieldExpression, context);
            case StaticMethodExpression staticMethodExpression ->
                    parseStaticMethodExpression(staticMethodExpression, context);
            case StringExpression stringExpression ->
                    parseStringExpression(stringExpression, context);
            case SymbolExpression symbolExpression ->
                    parseSymbolExpression(symbolExpression, context);
            case VarDefExpression varDefExpression ->
                    parseVarDefExpression(varDefExpression, context);
            case VarExpression varExpression -> parseVarExpression(varExpression, context);
            case StellaClassConstructExpression stellaClassConstructExpression ->
                    parseStellaClassConstructExpression(stellaClassConstructExpression, context);
            case StaticClassExpression staticClassExpression ->
                    parseStellaClassExpression(staticClassExpression, context);
            case StellaPackageExpression stellaPackageExpression ->
                    parseStellaPackageExpression(stellaPackageExpression, context);
            case UnitExpression unitExpression -> parseUnitExpression(unitExpression, context);
            case InstanceCreationExpression instanceCreationExpression ->
                    parseInstanceCreationExpression(instanceCreationExpression, context);
            case LocalVariableAssign localVariableAssign ->
                    parseLocalVariableAssign(localVariableAssign, context);
            case AssignExpression assignExpression -> parseAssign(assignExpression, context);
            case PutFieldExpression putFieldExpression ->
                    parsePutFieldExpression(putFieldExpression, context);
            case PutStaticFieldExpression putStaticFieldExpression -> parsePutStaticExpression(putStaticFieldExpression, context);
        }, expression.getClass().getSimpleName());
    }
    default Expression parsePutStaticExpression(PutStaticFieldExpression expression, C context) {
        return defaultBehavior(expression);
    }
    default Expression parsePutFieldExpression(PutFieldExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseAssign(AssignExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseLocalVariableAssign(LocalVariableAssign expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseInstanceCreationExpression(
            InstanceCreationExpression expression,
            C context) {
        return defaultBehavior(expression);
    }

    default Expression parseUnitExpression(UnitExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaPackageExpression(StellaPackageExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaClassExpression(StaticClassExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaClassConstructExpression(
            StellaClassConstructExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseVarExpression(VarExpression expression, C context) {
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


    default Expression parseStaticFieldExpression(StaticFieldExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticMethodExpression(StaticMethodExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseStringExpression(StringExpression expression, C context) {
        return defaultBehavior(expression);
    }

    default Expression parseSymbolExpression(SymbolExpression expression, C context) {
        return defaultBehavior(expression);
    }


    default Expression parseVarDefExpression(VarDefExpression expression, C context) {
        return defaultBehavior(expression);
    }

}
