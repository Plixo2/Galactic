package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

public record NumberExpression(Region region, BigDecimal value, PrimitiveType.StellaPrimitiveType type) implements Expression {

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
