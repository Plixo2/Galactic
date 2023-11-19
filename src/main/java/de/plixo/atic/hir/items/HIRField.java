package de.plixo.atic.hir.items;

import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HIRField {

    @Getter
    private final String name;
    @Getter
    private final HIRType type;
}
