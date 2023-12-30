package de.plixo.galactic.high_level.types;

import de.plixo.galactic.high_level.PrimitiveType;
import de.plixo.galactic.lexer.Region;

public record HIRPrimitive(Region region, PrimitiveType primitiveType) implements HIRType {
}
