package de.plixo.atic.tir;

import de.plixo.atic.types.Class;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Import {
    private final String alias;
    private final Class importedClass;
}
