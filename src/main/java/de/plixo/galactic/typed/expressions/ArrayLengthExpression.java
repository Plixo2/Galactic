package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;

public record ArrayLengthExpression(Region region, Expression expression) implements Expression {
    @Override
    public Type getType(Context context) {
        return PrimitiveType.INT;
    }
}
