package de.plixo.tir.expr;

import de.plixo.typesys.types.Type;

public sealed interface Expr
        permits BinExpr, BlockExpr, ConstRefExpr, ConstantExpr, DefinitionExpr, FunctionExpr,
        PathExpr, VariableExpr {
    Type getType();

    void fillType();
}

