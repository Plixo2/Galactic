package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class VariableExpr implements Expr{

    @Getter
    private final Scope.Variable variable;

    public VariableExpr(Scope.Variable variable) {
        this.variable = variable;
    }

    @Override
    public Type getType() {
        return variable.type();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("variable", variable.name());
        return jsonObject;
    }
}
