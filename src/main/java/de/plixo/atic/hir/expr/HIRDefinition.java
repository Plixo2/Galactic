package de.plixo.atic.hir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.parsing.records.VarDefinition;
import de.plixo.atic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

public final class HIRDefinition extends HIRExpr {

    @Getter
    private VarDefinition definition;

    public HIRDefinition(Region region, VarDefinition definition) {
        super(region);
        this.definition = definition;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "definition");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("name", definition.name());
        if (definition.typehint() != null) {
            jsonObject.add("typehint", definition.typehint().toJson());
        }
        jsonObject.add("expression", definition.expression().toJson());
        return jsonObject;
    }
}
