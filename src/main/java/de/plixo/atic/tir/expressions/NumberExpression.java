package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.PrimitiveType;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public final class NumberExpression extends Expression{

    private final BigDecimal value;
    private final PrimitiveType.APrimitiveType type;

    @Override
    public Type getType(Context context) {
        return new PrimitiveType(type);
    }

    public Object asObject() {
        return switch (type) {
            case INT, BYTE, SHORT, CHAR -> Integer.valueOf(value.intValue());
            case LONG -> value.longValue();
            case FLOAT -> value.floatValue();
            case DOUBLE -> value.doubleValue();
            case BOOLEAN -> throw new NullPointerException("Boolean not implemented");
        };
    }
}
