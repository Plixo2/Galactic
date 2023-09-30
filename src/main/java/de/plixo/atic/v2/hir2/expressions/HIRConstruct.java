package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.v2.hir2.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRConstruct implements HIRExpression {

    @Getter
    private final HIRType hirType;
    @Getter
    private final List<ConstructParam> parameters;


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.getClass().getSimpleName());
        jsonObject.add("class", hirType.toJson());
        var array = new JsonArray();
        parameters.forEach(arg -> array.add(arg.toJson()));
        jsonObject.add("parameters", array);
        return jsonObject;
    }

    @RequiredArgsConstructor
    public static class ConstructParam {
        @Getter
        private final String name;
        @Getter
        private final HIRExpression value;

        public JsonElement toJson() {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            jsonObject.add("value", value.toJson());
            return jsonObject;
        }
    }
}
