package de.plixo.atic.hir.parsing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.item.HIRAnnotation;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.lexer.Region;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArgDefinition {
    @Getter
    private final String name;
    @Getter
    private final @Nullable HIRType typeHint;

    @Getter
    private final @Nullable HIRExpr defaultValue;

    @Getter
    private final ArgDefineType defineType;

    @Getter
    private final Region region;

    @Getter
    private final List<HIRAnnotation> annotations;

    public ArgDefinition(Region region, String name, @Nullable HIRType typeHint,
                         @Nullable HIRExpr defaultValue, List<HIRAnnotation> annotations) {
        this.name = name;
        this.region = region;
        this.typeHint = typeHint;
        this.defaultValue = defaultValue;
        this.annotations = annotations;

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
        var jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.add("position", region().toJson());
        if (typeHint != null) {
            jsonObject.add("typeHint", typeHint.toJson());
        }
        if (defaultValue != null) {
            jsonObject.add("default", defaultValue.toJson());
        }
        return jsonObject;
    }

    public enum ArgDefineType {
        NOTHING,
        TYPE,
        VALUE,
        TYPE_VALUE
    }
}
