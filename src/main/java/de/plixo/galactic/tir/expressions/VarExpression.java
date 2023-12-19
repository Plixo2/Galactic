package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public final class VarExpression extends Expression {

    private final Scope.Variable variable;

    @Override
    public Type getType(Context context) {
        return Objects.requireNonNull(variable.getType(),
                "Variable type was not calculated, internal error");
    }

}
