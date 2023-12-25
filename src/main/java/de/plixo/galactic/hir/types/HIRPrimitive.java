package de.plixo.galactic.hir.types;

import de.plixo.galactic.common.PrimitiveType;
import de.plixo.galactic.lexer.Region;

public record HIRPrimitive(Region region, PrimitiveType primitiveType) implements HIRType {
}
