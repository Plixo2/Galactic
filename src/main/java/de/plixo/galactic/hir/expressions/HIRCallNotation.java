package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


public record HIRCallNotation(Region region, HIRExpression object, List<HIRExpression> arguments)
        implements HIRExpression {
}
