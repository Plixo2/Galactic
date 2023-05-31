package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class HIRReturn extends HIRExpr {
    @Getter
    private final @Nullable HIRExpr object;

    public HIRReturn(Region region, @Nullable HIRExpr object) {
        super(region);
        this.object = object;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "return");
        jsonObject.add("position", region().toJson());
        if (object != null) jsonObject.add("object", object.toJson());
        return jsonObject;
    }
}
