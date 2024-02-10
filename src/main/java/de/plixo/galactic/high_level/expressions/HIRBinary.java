package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.BinaryOperator;
import de.plixo.galactic.lexer.Region;

public record HIRBinary(Region region, HIRExpression left, BinaryOperator operator,
                        HIRExpression right) implements HIRExpression {


}
