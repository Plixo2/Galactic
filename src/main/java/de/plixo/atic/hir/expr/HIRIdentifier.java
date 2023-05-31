package de.plixo.atic.hir.expr;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class HIRIdentifier extends HIRExpr {

    @Getter
    private final String name;

    public HIRIdentifier(Region region, String name) {
        super(region);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "id");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("value", name);
        return jsonObject;
    }
}
