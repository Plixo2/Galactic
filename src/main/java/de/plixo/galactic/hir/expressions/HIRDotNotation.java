package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record HIRDotNotation(Region region, HIRExpression object, String id) implements HIRExpression {
}
