package de.plixo.atic.tir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

import java.util.List;

public final class BlockExpr implements Expr {
    @Getter
    private final List<Expr> expressions;

    @Getter
    private final Scope scope;

    public BlockExpr(List<Expr> expressions, Scope scope) {
        this.expressions = expressions;
        this.scope = scope;
    }

    @Override
    public Type getType() {
        if (expressions.isEmpty()) {
            return Primitive.VOID;
        }
        return expressions.get(expressions.size() - 1).getType();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        var args = new JsonArray();
        expressions.forEach(ref -> args.add(ref.toJson()));
        jsonObject.add("expressions", args);
        return jsonObject;
    }

}
