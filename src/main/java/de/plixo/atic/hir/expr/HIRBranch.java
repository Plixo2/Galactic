package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public final class HIRBranch extends HIRExpr {
    public HIRExpr condition;
    public HIRExpr body;
    public @Nullable HIRExpr elseBody;

    @Override
    public String toString() {
        if (elseBody == null) {
            return "if " + condition + body;
        }
        return "if " + condition +" " + body + " else " + elseBody;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "branch");
        jsonObject.add("condition", condition.toJson());
        jsonObject.add("body", body.toJson());
        if (elseBody != null) {
            jsonObject.add("elseBody", elseBody.toJson());
        }

        return jsonObject;
    }
}
