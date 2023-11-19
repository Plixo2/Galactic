package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;

public sealed interface HIRItem permits HIRBlock, HIRClass, HIRImport, HIRStaticMethod {

    JsonElement toJson();
    String toPrintName();
}
