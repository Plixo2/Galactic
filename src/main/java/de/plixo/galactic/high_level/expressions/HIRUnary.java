package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.UnaryOperator;
import de.plixo.galactic.lexer.Region;

public record HIRUnary(Region region, HIRExpression value, UnaryOperator operator)
        implements HIRExpression {


}
