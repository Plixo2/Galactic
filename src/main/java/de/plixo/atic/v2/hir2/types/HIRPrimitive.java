package de.plixo.atic.v2.hir2.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.v2.tir.type.Primitive;

public record HIRPrimitive(Region region, Primitive.PrimitiveType primitiveType)
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
