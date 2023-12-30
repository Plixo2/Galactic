package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.types.Type;

import java.util.Objects;

public record VarExpression(Region region, Scope.Variable variable) implements Expression {
    @Override
    public Type getType(Context context) {
        return Objects.requireNonNull(variable.getType(),
                "Variable type was not calculated, internal error");
    }

}
