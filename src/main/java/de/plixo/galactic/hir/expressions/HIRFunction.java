package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.items.HIRParameter;
import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;

import java.util.List;

public record HIRFunction(Region region, List<HIRParameter> HIRParameters, HIRType returnType,
                          HIRExpression expression) implements HIRExpression {
}
