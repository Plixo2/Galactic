package de.plixo.tir.expr;

import de.plixo.tir.scoping.Scope;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.Type;
import lombok.Getter;

import java.util.List;

public final class BlockExpr implements Expr {
    @Getter
    private final List<Expr> expressions;

    @Getter
    private final Scope scope;

    public BlockExpr(List<Expr> expressions, Scope scope) {
        this.expressions = expressions;
        this.scope = scope;
    }

    @Override
    public Type getType() {
        if (expressions.isEmpty()) {
            return Primitive.VOID;
        }
        return expressions.get(expressions.size() - 1).getType();
    }

    @Override
    public void fillType() {

    }
}
