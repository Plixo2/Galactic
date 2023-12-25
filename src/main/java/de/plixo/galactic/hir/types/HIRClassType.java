package de.plixo.galactic.hir.types;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.ObjectPath;

public record HIRClassType(Region region, ObjectPath path) implements HIRType {
}
