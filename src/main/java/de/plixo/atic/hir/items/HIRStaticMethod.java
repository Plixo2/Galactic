package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HIRStaticMethod implements HIRItem {
    @Getter
    private final HIRMethod hirMethod;
    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "import");
        jsonObject.addProperty("method", hirMethod.methodName());
        return jsonObject;
    }

    @Override
    public String toPrintName() {
        return "static Method-" + hirMethod.methodName();
    }
}
