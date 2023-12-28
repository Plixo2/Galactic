package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record HIRIdentifier(Region region, String id) implements HIRExpression {
}
