package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.UnaryFunctions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HIRUnary implements HIRExpression {

    @Getter
    private final HIRExpression object;
    @Getter
    private final UnaryFunctions function;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("object", object.toJson());
        jsonObject.addProperty("function", function.toString());
        return jsonObject;
    }
}
