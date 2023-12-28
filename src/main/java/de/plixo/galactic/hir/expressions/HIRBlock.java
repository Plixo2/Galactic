package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record HIRBlock(Region region, List<HIRExpression> expressions) implements HIRExpression {
}
