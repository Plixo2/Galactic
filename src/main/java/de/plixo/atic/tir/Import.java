package de.plixo.atic.tir;

import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.types.AClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Import {
    private final String alias;
    private final AClass importedClass;
}
