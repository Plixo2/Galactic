package de.plixo.atic.hir.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.common.PrimitiveType;
import de.plixo.atic.lexer.Region;

public record HIRPrimitive(Region region, PrimitiveType primitiveType)
        implements HIRType {
}
