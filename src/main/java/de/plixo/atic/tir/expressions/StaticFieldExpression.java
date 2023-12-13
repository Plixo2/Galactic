package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.sub.Field;
import de.plixo.atic.tir.Context;
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
