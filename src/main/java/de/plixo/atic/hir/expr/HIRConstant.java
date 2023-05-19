package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.Constant;
import lombok.Getter;

public final class HIRConstant extends HIRExpr {

    @Getter
    private final Constant constant;

    public HIRConstant(Constant constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return constant.toString();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "constant");
        jsonObject.addProperty("value", toString());
        return jsonObject;
    }

}
