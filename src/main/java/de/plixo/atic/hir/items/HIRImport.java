package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.utils.DotWordChain;
import de.plixo.atic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record HIRImport(Region region, @Nullable String importType, DotWordChain path)
        implements HIRItem {


    @Override
    public JsonElement toJson() {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "import");
        jsonObject.add("position", region().toJson());
        jsonObject.addProperty("path", path.asString());
        return jsonObject;
    }

    @Override
    public String toPrintName() {
        return "import-" + path.asString();
    }
}
