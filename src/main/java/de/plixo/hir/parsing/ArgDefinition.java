package de.plixo.hir.parsing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.hir.expr.HIRExpr;
import de.plixo.hir.typedef.HIRType;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class ArgDefinition {
    @Getter
    private final String name;
    @Getter
    private final @Nullable HIRType typeHint;

    @Getter
    private final @Nullable HIRExpr defaultValue;

    @Getter
    private final ArgDefineType defineType;

    public ArgDefinition(String name, @Nullable HIRType typeHint, @Nullable HIRExpr defaultValue) {
        this.name = name;
        this.typeHint = typeHint;
        this.defaultValue = defaultValue;

        if (typeHint == null && defaultValue == null) {
            this.defineType = ArgDefineType.NOTHING;
        } else if(typeHint != null) {
            if (defaultValue != null) {
                this.defineType = ArgDefineType.TYPE_VALUE;
            } else {
                this.defineType = ArgDefineType.TYPE;
            }
        } else {
            this.defineType = ArgDefineType.VALUE;
        }
    }

    @Override
    public String toString() {

        //do with StringBuilder
        if (typeHint == null) {
            if (defaultValue == null) {
                return name;
            } else {
                return name + " = " + defaultValue;
            }
        } else {
            if (defaultValue == null) {
                return name + ": " + typeHint;
            } else {
                return name + ": " + typeHint + " = " + defaultValue;
            }
        }
    }

    public JsonElement toJson() {
        var jsonElement = new JsonObject();
        jsonElement.addProperty("name", name);
        if (typeHint != null) {
            jsonElement.add("typeHint", typeHint.toJson());
        }
        if (defaultValue != null) {
            jsonElement.add("default", defaultValue.toJson());
        }
        return jsonElement;
    }

    public enum ArgDefineType {
        NOTHING,
        TYPE,
        VALUE,
        TYPE_VALUE
    }
}
