package de.plixo.atic.hir.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

import java.util.List;

public final class HIRImport extends HIRItem {
    @Getter
    private final List<String> path;
    @Getter
    private final boolean importAll;
    @Getter
    private final boolean useJVMInterface;

    public HIRImport(Region region, List<String> path, boolean importAll, boolean useJVMInterface,
                     List<HIRAnnotation> annotations) {
        super(region,annotations);
        this.path = path;
        this.importAll = importAll;
        this.useJVMInterface = useJVMInterface;
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "import");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("path" , name());
        jsonObject.addProperty("all" , importAll);
        return jsonObject;
    }

    @Override
    public String name() {
        return path.stream().reduce("", (a, b) -> a + "." + b).replaceFirst("\\.","");
    }
}
