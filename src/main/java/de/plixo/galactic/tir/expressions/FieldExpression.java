package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FieldExpression extends Expression {
    private final Expression object;
    private final Type owner;
    private final Field field;


    @Override
    public Type getType(Context context) {
        return field.type();
    }
}
