package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRClass implements HIRItem {
    @Getter
    private final String className;
    @Getter
    private final HIRType superClass;
    @Getter
    private final List<HIRType> interfaces;

    @Getter
    private final List<HIRField> fields;
    @Getter
    private final List<HIRMethod> methods;

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "class");
        jsonObject.addProperty("name", toPrintName());
        if (superClass != null) {
            jsonObject.add("extends", superClass.toJson());
        }
        return jsonObject;
    }

    @Override
    public String toPrintName() {
        return "class-" + className;
    }



}
