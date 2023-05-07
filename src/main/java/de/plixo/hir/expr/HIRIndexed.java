package de.plixo.hir.expr;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class HIRIndexed extends HIRExpr {

    public HIRExpr array;
    public HIRExpr argument;


    public HIRIndexed(HIRExpr array, HIRExpr argument) {
        this.array = array;
        this.argument = argument;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "indexed");
        jsonObject.add("prevExpr", array.toJson());
        jsonObject.add("index", argument.toJson());
        return jsonObject;
    }
}
