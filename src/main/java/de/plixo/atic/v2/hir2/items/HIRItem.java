package de.plixo.atic.v2.hir2.items;

import com.google.gson.JsonElement;

public sealed interface HIRItem permits HIRBlock, HIRClass, HIRFunction, HIRImport {

    JsonElement toJson();
    String name();
}
