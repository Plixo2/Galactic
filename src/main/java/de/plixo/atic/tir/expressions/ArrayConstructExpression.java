package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AArray;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ArrayConstructExpression extends Expression {

    @Getter
    private final AType elementType;

    @Getter
    private final List<Expression> values;

    @Override
    public AType getType(Context context) {
        return new AArray(elementType);
    }
}
