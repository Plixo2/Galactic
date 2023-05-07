package de.plixo.hir.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public final class HIRImport extends HIRItem {
    public List<String> path;
    public boolean importAll;

    public HIRImport(List<String> path, boolean importAll, List<HIRAnnotation> annotations) {
        super(annotations);
        this.path = path;
        this.importAll = importAll;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "import");
        jsonObject.addProperty("path" , name());
        jsonObject.addProperty("all" , importAll);
        return jsonObject;
    }

    @Override
    public String name() {
        return path.stream().reduce("", (a, b) -> a + "." + b).replaceFirst("\\.","");
    }
}
