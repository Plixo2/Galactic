package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

public final class HIRAssign extends HIRExpr {
    @Getter
    private final HIRExpr object;
    @Getter
    private final HIRExpr value;

    public HIRAssign(HIRExpr object, HIRExpr value) {
        this.object = object;
        this.value = value;
    }


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "assign");
        jsonObject.add("object", object.toJson());
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }
}
