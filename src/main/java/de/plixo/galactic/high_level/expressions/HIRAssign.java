package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;

public record HIRAssign(Region region, HIRExpression left, HIRExpression right)
        implements HIRExpression {

}
