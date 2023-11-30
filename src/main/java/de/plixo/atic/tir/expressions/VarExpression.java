package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Scope;
import de.plixo.atic.types.AType;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public final class VarExpression extends Expression {

    private final Scope.Variable variable;

    @Override
    public AType getType() {
        return Objects.requireNonNull(variable.getType(),
                "Variable type was not calculated, internal error");
    }

}
