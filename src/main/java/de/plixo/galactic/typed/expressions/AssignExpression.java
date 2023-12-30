package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public record AssignExpression(Region region, Expression left, Expression right)
        implements Expression {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
