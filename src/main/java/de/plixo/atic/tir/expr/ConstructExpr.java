package de.plixo.atic.tir.expr;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.StructImplementation;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

import java.util.List;

public final class ConstructExpr implements Expr {

    @Getter
    private final Unit.Structure structure;

    @Getter
    private final List<Expr> arguments;

    @Getter
    private final StructImplementation implementation;

    public ConstructExpr(Unit.Structure structure, List<Expr> arguments,
                         StructImplementation implementation) {
        this.structure = structure;
        this.arguments = arguments;
        this.implementation = implementation;
    }

    @Override
    public Type getType() {
        return implementation;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("class", this.getClass().getSimpleName());
        jsonObject.addProperty("struct", implementation.shortString());
        var args = new JsonArray();
        arguments.forEach(ref -> args.add(ref.toJson()));
        jsonObject.add("arguments", args);
        return jsonObject;
    }

}
