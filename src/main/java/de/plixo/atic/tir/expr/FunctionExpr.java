package de.plixo.atic.tir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.types.FunctionType;
import de.plixo.atic.typing.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FunctionExpr implements Expr {

    @Getter
    private final Scope scope;

    @Getter
    private final Type returnType;

    @Getter
    private final List<Variable> arguments;

    @Getter
    private final Type owner;

    @Getter
    @Setter
    @Accessors(fluent = false)
    private @Nullable Expr body = null;

    public FunctionExpr(Scope scope, Type returnType, List<Variable> arguments, Type owner) {
        this.scope = scope;
        this.returnType = returnType;
        this.arguments = arguments;
        this.owner = owner;
    }

    @Override
    public Type getType() {
        return new FunctionType(returnType, arguments.stream().map(ref -> ref.type).toList(),
                owner);
    }

    @AllArgsConstructor
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final Type type;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("returnType", returnType.shortString());
        jsonObject.addProperty("owner", owner.shortString());
        var args = new JsonArray();
        arguments.forEach(ref -> {
            var var = new JsonObject();
            var.addProperty("name", ref.name);
            var.addProperty("type", ref.type.shortString());
            args.add(var);
        });
        jsonObject.add("arguments", args);
        if (body != null) jsonObject.add("body", body.toJson());


        return jsonObject;
    }
}
