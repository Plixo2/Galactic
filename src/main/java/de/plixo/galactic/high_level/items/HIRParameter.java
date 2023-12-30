package de.plixo.galactic.high_level.items;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HIRParameter {
    private final Region region;
    private final String name;
    private final HIRType type;
}
