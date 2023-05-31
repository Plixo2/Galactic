package de.plixo.atic.lexer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.File;

public record Region(File file, Position left, Position right) {

    public static Region fromPosition(File file, Position position) {
        return new Region(file, position, position);
    }

    @Override
    public String toString() {
        return "(" + left.line() + ":" + left.from() + ")-(" + right.line() + ":" + right.to() +
                ")";
    }

    public JsonElement toJson() {
        // var jsonObject = new JsonObject();
        //jsonObject.addProperty("desc", toString);
        // return jsonObject;
        return new JsonPrimitive(toString());
    }

    public String debugFormat() {
        var path = file.getAbsolutePath();
        return path + ":" + (left.line()) + ":" + (left.from());
    }
}
