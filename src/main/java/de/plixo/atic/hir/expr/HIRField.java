package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class HIRField extends HIRExpr {

    @Getter
    private final HIRExpr object;

    @Getter
    private final String name;

    public HIRField(Region region, HIRExpr object, String name) {
        super(region);
        this.object = object;
        this.name = name;
    }

    @Override
    public String toString() {
        return object + "." + name;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "field");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("field", name);
        jsonObject.add("prevExpr", object.toJson());
        return jsonObject;
    }
}
