package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.types.PrimitiveType;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public final class HIRNumber implements HIRExpression {

    private final BigDecimal value;
    private final PrimitiveType.APrimitiveType type;

    public HIRNumber(String number) {
        if (number.isEmpty()) {
            value = new BigDecimal(number);
            type = PrimitiveType.APrimitiveType.DOUBLE;
            return;
        }
        var last = number.toLowerCase().charAt(number.length() - 1);
        if (String.valueOf(last).toLowerCase().matches("[iflbdsc]$")) {
            value = new BigDecimal(number.substring(0, number.length() - 1));
            type = switch (last) {
                case 'i' -> PrimitiveType.APrimitiveType.INT;
                case 'f' -> PrimitiveType.APrimitiveType.FLOAT;
                case 'l' -> PrimitiveType.APrimitiveType.LONG;
                case 'b' -> PrimitiveType.APrimitiveType.BYTE;
                case 'd' -> PrimitiveType.APrimitiveType.DOUBLE;
                case 's' -> PrimitiveType.APrimitiveType.SHORT;
                case 'c' -> PrimitiveType.APrimitiveType.CHAR;
                default -> throw new IllegalStateException(
                        "Unexpected value: " + number.charAt(number.length() - 1));
            };

        } else {
            if (number.contains(".")) {
                value = new BigDecimal(number);
                type = PrimitiveType.APrimitiveType.DOUBLE;
            } else {
                value = new BigDecimal(number);
                type = PrimitiveType.APrimitiveType.INT;
            }
        }
    }

}
