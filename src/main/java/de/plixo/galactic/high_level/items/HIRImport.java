package de.plixo.galactic.high_level.items;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.common.ObjectPath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a import statement
 */

@RequiredArgsConstructor
@Getter
public final class HIRImport implements HIRItem {
    private final Region region;
    /**
     * Alias of the import. Can be "*" to import all. "*" doesn't work with java yet
     */
    private final String name;
    private final @Nullable String importType;
    private final ObjectPath path;
}

