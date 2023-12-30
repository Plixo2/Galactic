package de.plixo.galactic.high_level.items;

import de.plixo.galactic.high_level.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HIRField {

    private final String name;
    private final HIRType type;
}
