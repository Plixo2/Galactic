package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AArray;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ArrayConstructExpression extends Expression {

    @Getter
    private final AType elementType;

    @Getter
    private final List<Expression> values;

    @Override
    public AType getType() {
        return new AArray(elementType);
    }
}
