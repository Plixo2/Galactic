package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.PrimitiveType;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class BooleanExpression extends Expression{

    @Getter
    private final boolean value;
    @Override
    public Type getType(Context context) {
        return new PrimitiveType(PrimitiveType.APrimitiveType.BOOLEAN);
    }
}
