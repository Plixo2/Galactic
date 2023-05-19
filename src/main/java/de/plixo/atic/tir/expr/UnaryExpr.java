package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.Operator;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class UnaryExpr implements Expr {

    @Getter
    private final Expr right;
    @Getter
    private final Operator operator;

    public UnaryExpr(Expr right, Operator operator) {
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Type getType() {
        return operator().checkAndGetType(right.getType());
    }


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("operator", operator.name());
        jsonObject.add("value", right.toJson());
        return jsonObject;
    }
}
