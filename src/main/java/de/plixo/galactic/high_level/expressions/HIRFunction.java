package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.items.HIRParameter;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;

import java.util.List;

public record HIRFunction(Region region, List<HIRParameter> HIRParameters, HIRType returnType,
                          HIRExpression expression, HIRType interfaceType) implements HIRExpression {
}
