package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

public record HIRIdentifier(Region region, String id) implements HIRExpression {
}
