package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;

public record SuperCallExpression(Region region) implements Expression {
    @Override
    public Type getType(Context context) {
        return null;
    }

}
