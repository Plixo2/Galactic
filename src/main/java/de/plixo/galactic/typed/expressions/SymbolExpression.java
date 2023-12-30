package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;

public record SymbolExpression(Region region, String id) implements Expression {
    @Override
    public Type getType(Context context) {
        throw new NullPointerException("Not implemented");
    }
}
