package de.plixo.atic.hir.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public final class HIRCallNotation implements HIRExpression {

    private final HIRExpression object;
    private final List<HIRExpression> arguments;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("object", object.toJson());
        var array = new JsonArray();
        arguments.forEach(arg -> array.add(arg.toJson()));
        jsonObject.add("arguments", array);
        return jsonObject;
    }
}
