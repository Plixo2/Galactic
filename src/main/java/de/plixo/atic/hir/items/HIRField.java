package de.plixo.atic.hir.items;

import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HIRField {

    private final String name;
    private final HIRType type;
}
