package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class HIRArrayAccessNotation implements HIRExpression {

    private final HIRExpression object;
    private final HIRExpression index;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("object", object.toJson());
        jsonObject.add("index", index.toJson());
        return jsonObject;
    }
}
