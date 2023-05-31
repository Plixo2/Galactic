package de.plixo.atic.hir.expr;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class HIRIndexed extends HIRExpr {

    @Getter
    private final HIRExpr array;
    @Getter
    private final HIRExpr argument;


    public HIRIndexed(Region region, HIRExpr array, HIRExpr argument) {
        super(region);
        this.array = array;
        this.argument = argument;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "indexed");
        jsonObject.add("position", region().toJson());
        jsonObject.add("prevExpr", array.toJson());
        jsonObject.add("index", argument.toJson());
        return jsonObject;
    }
}
