package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class HIRBranch implements HIRExpression {
    @Getter
    private final HIRExpression condition;
    @Getter
    private final HIRExpression body;

    @Getter
    private final @Nullable HIRExpression elseBody;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("condition", condition.toJson());
        jsonObject.add("body", body.toJson());
        if (elseBody != null) {
            jsonObject.add("elseBody", elseBody.toJson());
        }
        return jsonObject;
    }
}
