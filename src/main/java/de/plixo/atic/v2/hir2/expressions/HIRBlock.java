package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRBlock implements HIRExpression {

    @Getter
    private final List<HIRExpression> expressions;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        var array = new JsonArray();
        expressions.forEach(arg -> array.add(arg.toJson()));
        jsonObject.add("expressions", array);
        return jsonObject;
    }
}
