package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRIdentifier implements HIRExpression{
    @Getter
    private final String id;
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.addProperty("id", id);
        return jsonObject;
    }
}
