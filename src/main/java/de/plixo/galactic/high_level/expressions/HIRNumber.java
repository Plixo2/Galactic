package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.types.PrimitiveType;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public final class HIRNumber implements HIRExpression {

    private final Region region;
    private final BigDecimal value;
    private final PrimitiveType.StellaPrimitiveType type;

    public HIRNumber(Region region, String number) {
        this.region = region;
        if (number.isEmpty()) {
            value = new BigDecimal(number);
            type = PrimitiveType.StellaPrimitiveType.DOUBLE;
            return;
        }
        var last = number.toLowerCase().charAt(number.length() - 1);
        if (String.valueOf(last).toLowerCase().matches("[iflbdsc]$")) {
            value = new BigDecimal(number.substring(0, number.length() - 1));
            type = switch (last) {
                case 'i' -> PrimitiveType.StellaPrimitiveType.INT;
                case 'f' -> PrimitiveType.StellaPrimitiveType.FLOAT;
                case 'l' -> PrimitiveType.StellaPrimitiveType.LONG;
                case 'b' -> PrimitiveType.StellaPrimitiveType.BYTE;
                case 'd' -> PrimitiveType.StellaPrimitiveType.DOUBLE;
                case 's' -> PrimitiveType.StellaPrimitiveType.SHORT;
                case 'c' -> PrimitiveType.StellaPrimitiveType.CHAR;
                default -> throw new IllegalStateException(
                        "Unexpected value: " + number.charAt(number.length() - 1));
            };

        } else {
            if (number.contains(".")) {
                value = new BigDecimal(number);
                type = PrimitiveType.StellaPrimitiveType.DOUBLE;
            } else {
                value = new BigDecimal(number);
                type = PrimitiveType.StellaPrimitiveType.INT;
            }
        }
    }

}
