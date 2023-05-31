package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class HIRAssign extends HIRExpr {
    @Getter
    private final HIRExpr object;
    @Getter
    private final HIRExpr value;

    public HIRAssign(Region region, HIRExpr object, HIRExpr value) {
        super(region);
        this.object = object;
        this.value = value;
    }


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "assign");
        jsonObject.add("position", region().toJson());
        jsonObject.add("object", object.toJson());
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }
}
