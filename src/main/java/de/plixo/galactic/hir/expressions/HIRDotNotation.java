package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class HIRDotNotation implements HIRExpression {
    private final Region region;
    private final HIRExpression object;
    private final String id;

}
