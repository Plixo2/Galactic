package de.plixo.atic.v2.hir2.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.v2.hir2.types.HIRType;
import org.jetbrains.annotations.Nullable;

public record HIRClass(Region region, String name, @Nullable HIRType superClass)
        implements HIRItem {
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "class");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("name", name());
        if (superClass != null) {
            jsonObject.add("extends", superClass.toJson());
        }
        return jsonObject;
    }

    @Override
    public String name() {
        return "class-" + name;
    }
}
