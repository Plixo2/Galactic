package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

public record HIRWhile(Region region, HIRExpression condition, HIRExpression body)
        implements HIRExpression {

}
