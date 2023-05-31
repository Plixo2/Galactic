package de.plixo.atic.hir.typedef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HIRFunctionType extends HIRType {
    @Getter
    private final List<HIRType> parameters;
    @Getter
    private final @Nullable HIRType returnType;

    @Getter
    private final @Nullable HIRType owner;

    public HIRFunctionType(Region region, List<HIRType> parameters, @Nullable HIRType returnType,
                           @Nullable HIRType owner) {
        super(region);
        this.parameters = parameters;
        this.returnType = returnType;
        this.owner = owner;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "function");
        jsonObject.add("position", region().toJson());
        var parameters = new JsonArray();
        this.parameters.forEach(ref -> parameters.add(ref.toJson()));

        if (returnType != null) {
            jsonObject.add("returnType", returnType.toJson());
        }
        jsonObject.add("parameters", parameters);
        return jsonObject;
    }
}
