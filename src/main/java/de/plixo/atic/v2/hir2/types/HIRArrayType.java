package de.plixo.atic.v2.hir2.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;

public record HIRArrayType(Region region, HIRType type) implements HIRType {
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "primitiveType");
        jsonObject.add("position", region().toJson());
        jsonObject.add("arrayType", type.toJson());
        return jsonObject;
    }

    @Override
    public String name() {
        return "array-" + type.name();
    }
}
