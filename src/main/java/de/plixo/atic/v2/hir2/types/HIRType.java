package de.plixo.atic.v2.hir2.types;

import com.google.gson.JsonElement;

public sealed interface HIRType permits HIRArrayType, HIRClassType, HIRPrimitive {
    JsonElement toJson();
    String name();
}
