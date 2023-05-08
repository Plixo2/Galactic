package de.plixo.tir.expr;

import de.plixo.tir.scoping.Scope;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class DefinitionExpr implements Expr {

    @Getter
    private final Scope.Variable variable;

    public DefinitionExpr(Scope.Variable variable) {
        this.variable = variable;
    }

    @Override
    public Type getType() {
        return variable.type();
    }

    @Override
    public void fillType() {

    }
}
