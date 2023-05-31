package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.FunctionType;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class ChiselMethodRef implements Expr {

    @Getter
    private final FunctionType functionType;

    @Getter
    private final Unit.Constant constant;

    @Getter
    private final Expr expr;

    public ChiselMethodRef(FunctionType functionType, Unit.Constant constant, Expr expr) {
        this.functionType = functionType;
        this.constant = constant;
        this.expr = expr;
    }

    @Override
    public Type getType() {
        return functionType;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("constant", constant.absolutName());
        jsonObject.add("expr", expr.toJson());
        jsonObject.addProperty("type", functionType.shortString());
        return jsonObject;
    }
}
