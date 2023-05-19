package de.plixo.atic.hir.expr;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

public final class HIRIndexed extends HIRExpr {

    @Getter
    private final HIRExpr array;
    @Getter
    private final HIRExpr argument;


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
