package de.plixo.tir.expr;

import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class ConstRefExpr implements Expr {

    @Getter
    private final Unit.Constant constant;

    public ConstRefExpr(Unit.Constant constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void fillType() {

    }
}
