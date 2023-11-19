package de.plixo.atic.hir.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.utils.DotWordChain;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.tir.ObjectPath;

public record HIRClassType(ObjectPath path) implements HIRType {
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "classType");
        jsonObject.addProperty("path", path.asDotString());
        return jsonObject;
    }

    @Override
    public String name() {
        return "classType-" + path.asDotString();
    }
}
