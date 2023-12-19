package de.plixo.galactic.tir;

import de.plixo.galactic.types.Class;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Represents an imported class, with an alias
 */
@RequiredArgsConstructor
@Getter
public class Import {
    private final String alias;
    private final Class importedClass;
}
