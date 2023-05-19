package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.typing.types.StructImplementation;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class FieldAssignExpr implements Expr {

    @Getter
    private final Expr value;

    @Getter
    private final Expr structure;

    @Getter
    private final String fieldName;

    @Getter
    private final Type fieldType;

    @Getter
    private final StructImplementation structType;

    public FieldAssignExpr(Expr value, Expr structure, String fieldName, Type fieldType,
                           StructImplementation structType) {
        this.value = value;
        this.structure = structure;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.structType = structType;
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("field", fieldName);
        jsonObject.addProperty("fieldType", fieldType.shortString());
        jsonObject.addProperty("struct", structType.shortString());
        jsonObject.addProperty("value", structType.shortString());
        jsonObject.add("structure", structure.toJson());
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }
}
