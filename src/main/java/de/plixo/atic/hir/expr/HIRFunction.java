package de.plixo.atic.hir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.parsing.ArgDefinition;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Region;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public final class HIRFunction extends HIRExpr {

    public List<ArgDefinition> arguments;
    public @Nullable HIRType returnType;
    public @Nullable HIRType owner;
    public HIRExpr body;

    public HIRFunction(Region region, List<ArgDefinition> arguments, @Nullable HIRType returnType,
                       @Nullable HIRType owner, HIRExpr body) {
        super(region);
        this.arguments = arguments;
        this.returnType = returnType;
        this.owner = owner;
        this.body = body;
    }

    @Override
    public String toString() {
        var returnString = returnType == null ? "" : " -> " + returnType;
        return "fn (" + arguments + ")" + returnString + body;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "function");
        jsonObject.add("position", region().toJson());
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
