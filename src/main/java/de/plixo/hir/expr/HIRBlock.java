package de.plixo.hir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public final class HIRBlock extends HIRExpr {

    public final List<HIRExpr> statements;

    public HIRBlock(List<HIRExpr> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return statements.toString();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "block");
        var array = new JsonArray();
        statements.forEach(ref -> array.add(ref.toJson()));
        jsonObject.add("elements", array);

        return jsonObject;
    }
}
