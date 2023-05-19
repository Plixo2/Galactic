package de.plixo.atic.tir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class ConstantRefExpr implements Expr {

    @Getter
    private final Unit.Constant constant;

    public ConstantRefExpr(Unit.Constant constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return constant.type();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("const", constant.absolutName());
        return jsonObject;
    }


}
