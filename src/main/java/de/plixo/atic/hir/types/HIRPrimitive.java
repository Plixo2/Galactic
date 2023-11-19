package de.plixo.atic.hir.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.PrimitiveType;
import de.plixo.atic.lexer.Region;

public record HIRPrimitive(Region region, PrimitiveType primitiveType)
        implements HIRType {
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "primitiveType");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("primitive", primitiveType.name());
        return jsonObject;
    }

    @Override
    public String name() {
        return "primitive-" + primitiveType.name();
    }
}
