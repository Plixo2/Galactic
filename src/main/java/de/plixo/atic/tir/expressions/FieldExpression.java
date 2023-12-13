package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.sub.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FieldExpression extends Expression {
    private final Expression object;
    private final Field field;


    @Override
    public Type getType(Context context) {
        //TODO generics
        return field.type();
    }
}
