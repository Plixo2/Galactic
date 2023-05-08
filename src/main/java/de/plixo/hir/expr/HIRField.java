package de.plixo.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

public final class HIRField extends HIRExpr {

    @Getter
    private final HIRExpr object;

    @Getter
    private final String name;

    public HIRField(HIRExpr object, String name) {
        this.object = object;
        this.name = name;
    }

    @Override
    public String toString() {
        return object + "." + name;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "field");
        jsonObject.addProperty("field", name);
        jsonObject.add("prevExpr", object.toJson());
        return jsonObject;
    }
}
