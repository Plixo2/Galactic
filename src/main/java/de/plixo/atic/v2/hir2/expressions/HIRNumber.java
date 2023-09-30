package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
public final class HIRNumber implements HIRExpression {

    @Getter
    private final BigDecimal number;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.addProperty("number", number.toString());
        return jsonObject;
    }
}
