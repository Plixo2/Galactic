package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.Constant;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class HIRConstant extends HIRExpr {

    @Getter
    private final Constant constant;

    public HIRConstant(Region region, Constant constant) {
        super(region);
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
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("value", toString());
        return jsonObject;
    }

}
