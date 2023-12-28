package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

public record VarExpression(Region region, Scope.Variable variable) implements Expression {
    @Override
    public Type getType(Context context) {
        return Objects.requireNonNull(variable.getType(),
                "Variable type was not calculated, internal error");
    }

}
