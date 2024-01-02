package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.Objects;

/**
 * Tree is the interface for the compiler stages.
 *
 * @param <C> Context to parse with
 */
public interface Tree<C extends Context, A> {

    Expression defaultBehavior(Expression expression);

    default Expression parse(Expression expression, C context, A hint) {
        return Objects.requireNonNull(switch (expression) {
            case BlockExpression blockExpression ->
                    parseBlockExpression(blockExpression, context, hint);
            case BooleanExpression booleanExpression ->
                    parseBooleanExpression(booleanExpression, context, hint);
            case BranchExpression branchExpression ->
                    parseBranchExpression(branchExpression, context, hint);
            case CallNotation callNotation -> parseCallNotation(callNotation, context, hint);
            case ConstructExpression constructExpression ->
                    parseConstructExpression(constructExpression, context, hint);
            case DotNotation dotNotation -> parseDotNotation(dotNotation, context, hint);
            case MethodCallExpression methodCallExpression ->
                    parseMethodCallExpression(methodCallExpression, context, hint);
            case NumberExpression numberExpression ->
                    parseNumberExpression(numberExpression, context, hint);
            case FieldExpression fieldExpression ->
                    parseObjectFieldExpression(fieldExpression, context, hint);
            case GetMethodExpression methodExpression ->
                    parseObjectMethodExpression(methodExpression, context, hint);
            case StaticFieldExpression staticFieldExpression ->
                    parseStaticFieldExpression(staticFieldExpression, context, hint);
            case StaticMethodExpression staticMethodExpression ->
                    parseStaticMethodExpression(staticMethodExpression, context, hint);
            case StringExpression stringExpression ->
                    parseStringExpression(stringExpression, context, hint);
            case SymbolExpression symbolExpression ->
                    parseSymbolExpression(symbolExpression, context, hint);
            case VarDefExpression varDefExpression ->
                    parseVarDefExpression(varDefExpression, context, hint);
            case VarExpression varExpression -> parseVarExpression(varExpression, context, hint);
            case StellaClassConstructExpression stellaClassConstructExpression ->
                    parseStellaClassConstructExpression(stellaClassConstructExpression, context,
                            hint);
            case StaticClassExpression staticClassExpression ->
                    parseStellaClassExpression(staticClassExpression, context, hint);
            case StellaPackageExpression stellaPackageExpression ->
                    parseStellaPackageExpression(stellaPackageExpression, context, hint);
            case UnitExpression unitExpression ->
                    parseUnitExpression(unitExpression, context, hint);
            case InstanceCreationExpression instanceCreationExpression ->
                    parseInstanceCreationExpression(instanceCreationExpression, context, hint);
            case LocalVariableAssign localVariableAssign ->
                    parseLocalVariableAssign(localVariableAssign, context, hint);
            case AssignExpression assignExpression -> parseAssign(assignExpression, context, hint);
            case PutFieldExpression putFieldExpression ->
                    parsePutFieldExpression(putFieldExpression, context, hint);
            case PutStaticFieldExpression putStaticFieldExpression ->
                    parsePutStaticExpression(putStaticFieldExpression, context, hint);
            case CastExpression castExpression ->
                    parseCastExpression(castExpression, context, hint);
            case CastCheckExpression castCheckExpression ->
                    parseCastCheckExpression(castCheckExpression, context, hint);
        }, expression.getClass().getSimpleName());
    }


    default Expression parseCastCheckExpression(CastCheckExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseCastExpression(CastExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parsePutStaticExpression(PutStaticFieldExpression expression, C context,
                                                A hint) {
        return defaultBehavior(expression);
    }

    default Expression parsePutFieldExpression(PutFieldExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseAssign(AssignExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseLocalVariableAssign(LocalVariableAssign expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseInstanceCreationExpression(InstanceCreationExpression expression,
                                                       C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseUnitExpression(UnitExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaPackageExpression(StellaPackageExpression expression, C context,
                                                    A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaClassExpression(StaticClassExpression expression, C context,
                                                  A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseStellaClassConstructExpression(
            StellaClassConstructExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseVarExpression(VarExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }


    default Expression parseBlockExpression(BlockExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseBooleanExpression(BooleanExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseBranchExpression(BranchExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseCallNotation(CallNotation expression, C context, A hint) {
        return defaultBehavior(expression);
    }


    default Expression parseConstructExpression(ConstructExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseDotNotation(DotNotation expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseMethodCallExpression(MethodCallExpression expression, C context,
                                                 A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseNumberExpression(NumberExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectFieldExpression(FieldExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseObjectMethodExpression(GetMethodExpression expression, C context,
                                                   A hint) {
        return defaultBehavior(expression);
    }


    default Expression parseStaticFieldExpression(StaticFieldExpression expression, C context,
                                                  A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseStaticMethodExpression(StaticMethodExpression expression, C context,
                                                   A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseStringExpression(StringExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

    default Expression parseSymbolExpression(SymbolExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }


    default Expression parseVarDefExpression(VarDefExpression expression, C context, A hint) {
        return defaultBehavior(expression);
    }

}
