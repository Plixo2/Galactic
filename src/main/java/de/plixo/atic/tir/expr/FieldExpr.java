package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.typing.types.StructImplementation;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public final class FieldExpr implements Expr {

    @Getter
    private final Expr structure;

    @Getter
    private final String field;

    @Getter
    private final StructImplementation structImplementation;

    @Getter
    private final Type fieldType;

    @Getter
    private final Type owner;


    public FieldExpr(Expr structure, String field, StructImplementation structImplementation,
                     Type fieldType, Type owner) {
        this.structure = structure;
        this.field = field;
        this.structImplementation = structImplementation;
        this.fieldType = fieldType;
        this.owner = owner;
    }

//    @Override
//    public @Nullable Type getOwner() {
//        return owner;
//    }

    @Override
    public Type getType() {
        return fieldType;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("field", field);
        jsonObject.addProperty("fieldType", fieldType.shortString());
        jsonObject.addProperty("struct", structImplementation.shortString());
        jsonObject.add("structure", structure.toJson());
        return jsonObject;
    }
}
