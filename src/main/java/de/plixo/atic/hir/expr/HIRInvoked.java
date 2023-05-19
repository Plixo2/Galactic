package de.plixo.atic.hir.expr;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.List;

public final class HIRInvoked extends HIRExpr {

    @Getter
    private final HIRExpr function;

    @Getter
    private final List<HIRExpr> arguments;


    public HIRInvoked(HIRExpr function, List<HIRExpr> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "invoked");
        jsonObject.add("left", function.toJson());
        var array = new JsonArray();
        arguments.forEach(ref -> array.add(ref.toJson()));
        jsonObject.add("arguments",array);
        return jsonObject;
    }
}
