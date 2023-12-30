package de.plixo.galactic.high_level.types;

import de.plixo.galactic.lexer.Region;

public record HIRArrayType(Region region, HIRType type) implements HIRType {

}
