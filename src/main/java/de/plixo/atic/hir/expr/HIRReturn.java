package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class HIRReturn extends HIRExpr {
    @Getter
    private final @Nullable HIRExpr object;

    public HIRReturn(@Nullable HIRExpr object) {
        this.object = object;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "return");
        if (object != null) jsonObject.add("object", object.toJson());
        return jsonObject;
    }
}
