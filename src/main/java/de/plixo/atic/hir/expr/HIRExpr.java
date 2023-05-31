package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public abstract sealed class HIRExpr
        permits HIRAssign, HIRBinOp, HIRBlock, HIRBranch, HIRConstant, HIRDefinition, HIRField,
        HIRFunction, HIRIdentifier, HIRIndexed, HIRInvoked, HIRReturn, HIRUnary {

    @Getter
    private final Region region;

    public HIRExpr(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "HIRExpr " + this.getClass().getName();
    }

    public abstract JsonElement toJson();
}
