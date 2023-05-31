package de.plixo.atic.hir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;

import java.util.List;

public final class HIRBlock extends HIRExpr {

    public final List<HIRExpr> statements;

    public HIRBlock(Region region, List<HIRExpr> statements) {
        super(region);
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
        jsonObject.add("position", region().toJson());
        var array = new JsonArray();
        statements.forEach(ref -> array.add(ref.toJson()));
        jsonObject.add("elements", array);

        return jsonObject;
    }
}
