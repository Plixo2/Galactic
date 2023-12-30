package de.plixo.galactic.high_level.types;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.common.ObjectPath;

public record HIRClassType(Region region, ObjectPath path) implements HIRType {
}
