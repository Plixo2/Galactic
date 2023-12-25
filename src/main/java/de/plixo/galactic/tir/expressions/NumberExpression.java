package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public final class NumberExpression extends Expression {

    private final Region region;
    private final BigDecimal value;
    private final PrimitiveType.StellaPrimitiveType type;

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
