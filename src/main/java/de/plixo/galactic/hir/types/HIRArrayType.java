package de.plixo.galactic.hir.types;

import de.plixo.galactic.lexer.Region;

public record HIRArrayType(Region region, HIRType type) implements HIRType {

}
