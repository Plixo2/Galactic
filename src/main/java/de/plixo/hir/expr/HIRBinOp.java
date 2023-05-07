package de.plixo.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.common.Operator;

public final class HIRBinOp extends HIRExpr {

    public HIRExpr left;
    public HIRExpr right;
    public Operator operator;

    public HIRBinOp(HIRExpr left, HIRExpr right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return left + " " + operator.symbol() + " " + right;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "binOp");
        jsonObject.addProperty("operator", operator.symbol());
        jsonObject.add("left",left.toJson());
        jsonObject.add("right",right.toJson());

        return jsonObject;
    }
}
