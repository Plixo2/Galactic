package de.plixo.galactic.hir.items;

import de.plixo.galactic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HIRField {

    private final String name;
    private final HIRType type;
}
