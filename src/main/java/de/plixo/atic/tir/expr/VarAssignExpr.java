package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class VarAssignExpr implements Expr {

    @Getter
    private final Expr value;

    @Getter
    private final Scope.Variable variable;

    public VarAssignExpr(Expr value, Scope.Variable variable) {
        this.value = value;
        this.variable = variable;
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("variable", variable.name());
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }
}
