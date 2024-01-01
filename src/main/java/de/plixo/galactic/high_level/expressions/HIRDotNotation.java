package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

public record HIRDotNotation(Region region, HIRExpression object, String id)
        implements HIRExpression {
}
