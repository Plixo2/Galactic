package de.plixo.atic.hir.typedef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public final class HIRClassType extends HIRType {

    @Getter
    private final List<String> path;

    @Getter
    private final List<HIRType> generics;

    public HIRClassType(Region region, List<String> path, List<HIRType> generics) {
        super(region);
        this.path = path;
        this.generics = generics;
    }

    @Override
    public String toString() {
        return "Class " + path.stream().reduce("", (a,b) -> a + "." + b).replaceFirst("\\.","") +
                "<" + generics + ">";
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "class");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("path", path.stream().reduce("", (a,b) -> a + "." + b).replaceFirst("\\.",""));
        var generics = new JsonArray();
        this.generics.forEach(ref -> generics.add(ref.toJson()));
        jsonObject.add("generics", generics);
        return jsonObject;
    }
}
