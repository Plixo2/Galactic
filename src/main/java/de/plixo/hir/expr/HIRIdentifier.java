package de.plixo.hir.expr;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

public final class HIRIdentifier extends HIRExpr {

    @Getter
    private final String name;

    public HIRIdentifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "id");
        jsonObject.addProperty("value", name);
        return jsonObject;
    }
}
