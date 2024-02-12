package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;

public record ThisExpression(Region region, Type type) implements Expression {
    @Override
    public Type getType(Context context) {
        return type;
    }
}
