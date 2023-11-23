package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Scope;
import de.plixo.atic.types.AType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class VarExpression extends Expression {

    private final Scope.Variable variable;
    @Override
    public AType getType() {
        throw new NullPointerException("Not implemented");
    }
}
