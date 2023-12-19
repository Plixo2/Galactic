package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StaticFieldExpression extends Expression {
    @Getter
    private final Class aClass;
    @Getter
    private final Field field;


    @Override
    public Type getType(Context context) {
        return field.type();
    }
}
