package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.utils.DotWordChain;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.tir.ObjectPath;
import org.jetbrains.annotations.Nullable;

public record HIRImport(Region region, String name, @Nullable String importType, ObjectPath path)
        implements HIRItem {


}
