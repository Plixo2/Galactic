package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record HIRAssign(Region region, HIRExpression left, HIRExpression right) implements HIRExpression {

}
