package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.types.APrimitive;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public final class HIRNumber implements HIRExpression {

    private final BigDecimal value;
    private final APrimitive.APrimitiveType type;

    public HIRNumber(String number) {
        if (number.isEmpty()) {
            value = new BigDecimal(number);
            type = APrimitive.APrimitiveType.DOUBLE;
            return;
        }
        var last = number.toLowerCase().charAt(number.length() - 1);
        if (String.valueOf(last).toLowerCase().matches("[iflbdsc]$")) {
            value = new BigDecimal(number.substring(0, number.length() - 1));
            type = switch (last) {
                case 'i' -> APrimitive.APrimitiveType.INT;
                case 'f' -> APrimitive.APrimitiveType.FLOAT;
                case 'l' -> APrimitive.APrimitiveType.LONG;
                case 'b' -> APrimitive.APrimitiveType.BYTE;
                case 'd' -> APrimitive.APrimitiveType.DOUBLE;
                case 's' -> APrimitive.APrimitiveType.SHORT;
                case 'c' -> APrimitive.APrimitiveType.CHAR;
                default -> throw new IllegalStateException(
                        "Unexpected value: " + number.charAt(number.length() - 1));
            };

        } else {
            value = new BigDecimal(number);
            type = APrimitive.APrimitiveType.DOUBLE;
        }
    }


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.addProperty("number", value.toString());
        jsonObject.addProperty("type", type.toString());
        return jsonObject;
    }
}
