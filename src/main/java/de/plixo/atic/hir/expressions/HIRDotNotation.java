package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRDotNotation implements HIRExpression{

    @Getter
    private final HIRExpression object;
    @Getter
    private final String id;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.addProperty("id", id);
        jsonObject.add("object", object.toJson());
        return jsonObject;
    }
}
