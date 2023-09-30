package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class NumberExpression extends Expression {
    private final BigDecimal number;

    @Override
    public Type asAticType() {
        return Primitive.DOUBLE;
    }
}
