package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class NumberExpression extends Expression{

    @Getter
    private final BigDecimal value;

    @Override
    public AType getType() {
        return new APrimitive(APrimitive.APrimitiveType.DOUBLE);
    }
}
