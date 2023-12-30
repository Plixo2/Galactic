package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

import java.util.List;


public record HIRCallNotation(Region region, HIRExpression object, List<HIRExpression> arguments)
        implements HIRExpression {
}
