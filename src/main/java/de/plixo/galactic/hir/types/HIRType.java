package de.plixo.galactic.hir.types;

import de.plixo.galactic.lexer.Region;

public sealed interface HIRType permits HIRArrayType, HIRClassType, HIRPrimitive {
    Region region();
}
