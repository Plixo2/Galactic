package de.plixo.atic.hir.items;

import de.plixo.atic.lexer.Region;
import de.plixo.atic.tir.ObjectPath;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a import statement
 * @param region
 * @param name alias of the import. Can be "*" to import all. "*" doesn't work with java yet
 * @param importType
 * @param path
 */
public record HIRImport(Region region, String name, @Nullable String importType, ObjectPath path)
        implements HIRItem {


}
