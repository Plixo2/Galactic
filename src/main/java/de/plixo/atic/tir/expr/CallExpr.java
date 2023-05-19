package de.plixo.atic.tir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.typing.types.FunctionType;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

import java.util.List;

public final class CallExpr implements Expr {

    @Getter
    private final Expr function;

    @Getter
    private final FunctionType functionType;

    @Getter
    private final List<Expr> arguments;

    @Getter
    private final Type returnType;

    public CallExpr(Expr function, FunctionType functionType, List<Expr> arguments,
                    Type returnType) {
        this.function = function;
        this.functionType = functionType;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("returnType", returnType.shortString());
        jsonObject.addProperty("functionType", functionType.shortString());
        var args = new JsonArray();
        arguments.forEach(ref -> args.add(ref.toJson()));
        jsonObject.add("arguments", args);
        jsonObject.add("function", function.toJson());
        return jsonObject;
    }
}
