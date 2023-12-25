package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FieldExpression extends Expression {
    private final Region region;
    private final Expression object;
    private final Type owner;
    private final Field field;


    @Override
    public Type getType(Context context) {
        return field.type();
    }
}
