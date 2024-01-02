package de.plixo.galactic.high_level.types;

import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.lexer.Region;

public record HIRClassType(Region region, ObjectPath path) implements HIRType {
}
