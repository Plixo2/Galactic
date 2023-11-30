package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public final class NumberExpression extends Expression{

    private final BigDecimal value;
    private final APrimitive.APrimitiveType type;

    @Override
    public AType getType() {
        return new APrimitive(type);
    }
}
