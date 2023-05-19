package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.Constant;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class ConstantExpr implements Expr {

    @Getter
    private final Constant constant;

    public ConstantExpr(Constant constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return switch (constant) {
            case Constant.BoolConstant ignored -> Primitive.BOOL;
            case Constant.NumberConstant numberConstant -> {
                if (numberConstant.number instanceof Double) {
                    yield Primitive.FLOAT;
                } else {
                    yield Primitive.INT;
                }
            }
            case Constant.StringConstant ignored -> Primitive.STRING;
        };
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("const", constant.toString());
        return jsonObject;
    }

}
