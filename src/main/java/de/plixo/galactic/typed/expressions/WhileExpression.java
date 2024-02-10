package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public record WhileExpression(Region region, Expression condition, Expression body)
        implements Expression {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }

}
