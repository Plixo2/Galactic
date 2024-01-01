package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;

public record HIRCastCheck(Region region, HIRExpression object, HIRType type)
        implements HIRExpression {
}
