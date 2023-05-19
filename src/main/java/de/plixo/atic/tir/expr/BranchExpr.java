package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.scoping.Scope;
import de.plixo.atic.typing.TypeQuery;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class BranchExpr implements Expr {

    @Getter
    private final Expr condition;

    @Getter
    private final Expr body;
    @Getter
    private final Scope bodyScope;

    @Getter
    private final @Nullable Expr elseBody;
    @Getter
    private final @Nullable Scope elseBodyScope;


    public BranchExpr(Expr condition, Expr body, Scope bodyScope, @Nullable Expr elseBody,
                      @Nullable Scope elseBodyScope) {
        this.condition = condition;
        this.body = body;
        this.bodyScope = bodyScope;
        this.elseBody = elseBody;
        this.elseBodyScope = elseBodyScope;
    }

    @Override
    public Type getType() {
        if (elseBody != null) {
            var typeQuery = new TypeQuery(body.getType(), elseBody.getType());
            if (typeQuery.test()) {
                typeQuery.mutate();
                return body().getType();
            }
        }
        return Primitive.VOID;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.add("condition", condition.toJson());
        jsonObject.add("body", body.toJson());
        if (elseBody != null) jsonObject.add("else", elseBody.toJson());
        return jsonObject;
    }

}
