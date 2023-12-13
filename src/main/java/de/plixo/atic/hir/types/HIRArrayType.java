package de.plixo.atic.hir.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.lexer.Region;

public record HIRArrayType(Region region, HIRType type) implements HIRType {

}
