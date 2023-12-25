package de.plixo.galactic.hir.items;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.ObjectPath;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a import statement
 *
 * @param region
 * @param name       alias of the import. Can be "*" to import all. "*" doesn't work with java yet
 * @param importType
 * @param path
 */
public record HIRImport(Region region, String name, @Nullable String importType, ObjectPath path)
        implements HIRItem {


}
