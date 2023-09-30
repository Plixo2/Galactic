package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import de.plixo.atic.typing.types.Type;

public sealed interface Expr
        permits BinExpr, BlockExpr, BranchExpr, CallExpr, ConstantExpr, ChiselMethodRef,
        ConstantRefExpr, StructConstructExpr, DefinitionExpr, FieldAssignExpr, FieldExpr, FunctionExpr,
        PathExpr, ReturnExpr, UnaryExpr, VarAssignExpr, VariableExpr {
    Type getType();



//    default @Nullable Type getOwner() {
//        return null;
//    }

    JsonElement toJson();
}

