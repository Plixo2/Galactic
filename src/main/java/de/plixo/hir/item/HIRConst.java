package de.plixo.hir.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.hir.expr.HIRExpr;
import de.plixo.hir.typedef.HIRType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HIRConst extends HIRItem {
    public String name;
    public HIRExpr expression;
    public @Nullable HIRType typehint;

    public HIRConst(String name, HIRExpr expression, @Nullable HIRType typehint, List<HIRAnnotation> annotations) {
        super(annotations);
        this.name = name;
        this.expression = expression;
        this.typehint = typehint;
    }

    @Override
    public String toString() {
        return "HIRConst " + name + " = " + expression;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "const");
        jsonObject.addProperty("name", name);
        jsonObject.add("expression", expression.toJson());
        if (typehint != null) {
            jsonObject.add("typeHint", typehint.toJson());
        }
        return jsonObject;
    }

    @Override
    public String name() {
        return name;
    }
}
