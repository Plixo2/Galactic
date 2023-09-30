package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.v2.tir.expressions.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HIRBranch implements HIRExpression {
    @Getter
    private final HIRExpression condition;
    @Getter
    private final HIRExpression body;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("condition", condition.toJson());
        jsonObject.add("body", body.toJson());
        return jsonObject;
    }
}
