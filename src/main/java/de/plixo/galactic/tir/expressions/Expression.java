package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;

/**
 * Base class for all expressions,
 * Not all Expressions are used in all stages
 * see flow.md
 */
public sealed abstract class Expression
        permits AssignExpression, BlockExpression, BooleanExpression, BranchExpression,
        CallNotation, ConstructExpression, DotNotation, FieldExpression, GetMethodExpression,
        InstanceCreationExpression, LocalVariableAssign, MethodCallExpression, NumberExpression,
        PutFieldExpression, PutStaticFieldExpression, StaticClassExpression, StaticFieldExpression,
        StaticMethodExpression, StellaClassConstructExpression, StellaPackageExpression,
        StringExpression, SymbolExpression, UnitExpression, VarDefExpression, VarExpression {


    public abstract Type getType(Context context);
    public abstract Region region();
}
