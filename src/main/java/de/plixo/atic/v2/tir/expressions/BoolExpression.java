package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BoolExpression extends Expression {
    private final boolean state;

    @Override
    public Type asAticType() {
        return Primitive.BOOLEAN;
    }
}
