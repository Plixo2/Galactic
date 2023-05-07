package de.plixo.hir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.hir.parsing.ArgDefinition;
import de.plixo.hir.typedef.HIRType;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;


@AllArgsConstructor
public final class HIRFunction extends HIRExpr {

    public List<ArgDefinition> arguments;
    public @Nullable HIRType returnType;
    public HIRExpr body;

    @Override
    public String toString() {
        var returnString = returnType == null ? "" : " -> " + returnType;
        return "fn (" + arguments + ")" + returnString + body;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "function");
        var array = new JsonArray();
        arguments.forEach(ref -> array.add(ref.toJson()));
        jsonObject.add("arguments",array);
        if (returnType != null) {
            jsonObject.add("returnType",returnType.toJson());
        }
        jsonObject.add("body", body.toJson());
        return jsonObject;
    }
}
