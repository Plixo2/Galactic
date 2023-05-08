package de.plixo.tir.expr;

import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class ConstantRefExpr implements Expr {

    @Getter
    private final Unit.Constant constant;

    public ConstantRefExpr(Unit.Constant constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return constant.type();
    }

    @Override
    public void fillType() {

    }
}
