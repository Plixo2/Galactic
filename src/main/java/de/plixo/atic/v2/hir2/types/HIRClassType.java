package de.plixo.atic.v2.hir2.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.v2.hir2.utils.DotWordChain;

public record HIRClassType(Region region, DotWordChain path) implements HIRType {
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "classType");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("path", path.asString());
        return jsonObject;
    }

    @Override
    public String name() {
        return "classType-" + path.asString();
    }
}
