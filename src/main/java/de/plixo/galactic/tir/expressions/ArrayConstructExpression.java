package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.ArrayType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ArrayConstructExpression extends Expression {

    @Getter
    private final Type elementType;

    @Getter
    private final List<Expression> values;

    @Override
    public Type getType(Context context) {
        return new ArrayType(elementType);
    }
}
