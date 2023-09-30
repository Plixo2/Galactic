package de.plixo.atic.v2.hir2.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.hir2.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRFunction implements HIRItem {

    @Getter
    private final String methodName;
    @Getter
    private final List<Parameter> parameters;
    @Getter
    private final HIRType returnType;

    @Getter
    private final HIRExpression expression;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.addProperty("name", methodName);
        jsonObject.add("output", returnType.toJson());
        var array = new JsonArray();
        for (var parameter : parameters) {
            array.add(parameter.toJson());
        }
        jsonObject.add("params", array);
        jsonObject.add("expression", expression.toJson());
        return jsonObject;
    }

    @Override
    public String name() {
        return "function-" + methodName;
    }

    @RequiredArgsConstructor
    public static class Parameter {
        @Getter
        private final String name;
        @Getter
        private final HIRType type;

        public JsonElement toJson() {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            jsonObject.add("type", type.toJson());
            return jsonObject;
        }
    }
}
