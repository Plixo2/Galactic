package de.plixo.hir.typedef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public final class HIRClassType extends HIRType {

    @Getter
    private List<String> path;

    @Getter
    private List<HIRType> generics;

    @Override
    public String toString() {
        return "Class " + path.stream().reduce("", (a,b) -> a + "." + b).replaceFirst("\\.","") +
                "<" + generics + ">";
    }

    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "class");
        jsonObject.addProperty("path", path.stream().reduce("", (a,b) -> a + "." + b).replaceFirst("\\.",""));
        var generics = new JsonArray();
        this.generics.forEach(ref -> generics.add(ref.toJson()));
        jsonObject.add("generics", generics);
        return jsonObject;
    }
}
