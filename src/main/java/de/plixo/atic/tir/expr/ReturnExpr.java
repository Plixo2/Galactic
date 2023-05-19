package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class ReturnExpr implements Expr {

    @Getter
    private final @Nullable Expr value;

    public ReturnExpr(@Nullable Expr value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        if (value != null) {
            return value.getType();
        }
        return Primitive.VOID;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        if (value != null) {
            jsonObject.add("value", value.toJson());
        }
        return jsonObject;
    }
}
