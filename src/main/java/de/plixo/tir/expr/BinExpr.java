package de.plixo.tir.expr;

import de.plixo.common.Operator;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class BinExpr implements Expr {

    @Getter
    private final Expr left;
    @Getter
    private final Expr right;
    @Getter
    private final Operator operator;

    public BinExpr(Expr left, Expr right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Type getType() {
        return operator().checkAndGetType(left.getType(),right.getType());
    }

    @Override
    public void fillType() {

    }
}
