package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

public record HIRString(Region region, String string) implements HIRExpression {
}
