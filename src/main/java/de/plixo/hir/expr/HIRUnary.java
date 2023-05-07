package de.plixo.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.common.Operator;

public final class HIRUnary extends HIRExpr {

    public HIRExpr left;
    public Operator operator;

    public HIRUnary(HIRExpr left, Operator operator) {
        this.left = left;
        this.operator = operator;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "unary");
        jsonObject.add("prevExpr", left.toJson());
        return jsonObject;
    }
}
