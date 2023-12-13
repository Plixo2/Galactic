package de.plixo.atic.hir.types;

import com.google.gson.JsonElement;

public sealed interface HIRType permits HIRArrayType, HIRClassType, HIRPrimitive {
}
