package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class DefinitionExpr implements Expr {

    @Getter
    private final Scope.Variable variable;

    @Getter
    private final Expr expression;

    public DefinitionExpr(Scope.Variable variable, Expr expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public Type getType() {
        return variable.type();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("name", variable.name());
        jsonObject.add("value", expression.toJson());
        return jsonObject;
    }
}
