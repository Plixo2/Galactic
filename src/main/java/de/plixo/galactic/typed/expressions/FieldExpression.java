package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Type;

public record FieldExpression(Region region, Expression object, Type owner, Field field)
        implements Expression {
    @Override
    public Type getType(Context context) {
        return field.type();
    }
}
