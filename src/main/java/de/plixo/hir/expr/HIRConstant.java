package de.plixo.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.common.Constant;
import lombok.AllArgsConstructor;

public final class HIRConstant extends HIRExpr {

    public Constant constant;

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
