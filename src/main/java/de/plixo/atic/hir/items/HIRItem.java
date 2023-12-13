package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;

public sealed interface HIRItem permits HIRTopBlock, HIRClass, HIRImport, HIRStaticMethod {

}
