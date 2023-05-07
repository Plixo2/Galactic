package de.plixo.hir.typedef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public final class HIRFunctionType extends HIRType {
    @Getter
    private List<HIRType> parameters;
    @Getter
    private @Nullable HIRType returnType;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "function");
        var parameters = new JsonArray();
        this.parameters.forEach(ref -> parameters.add(ref.toJson()));

        if (returnType != null) {
            jsonObject.add("returnType", returnType.toJson());
        }
        jsonObject.add("parameters", parameters);
        return jsonObject;
    }
}
