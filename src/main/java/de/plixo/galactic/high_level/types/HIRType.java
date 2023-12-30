package de.plixo.galactic.high_level.types;

import de.plixo.galactic.lexer.Region;

public sealed interface HIRType permits HIRArrayType, HIRClassType, HIRPrimitive {
    Region region();
}
