package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

import java.util.List;

public record HIRBlock(Region region, List<HIRExpression> expressions) implements HIRExpression {
}
