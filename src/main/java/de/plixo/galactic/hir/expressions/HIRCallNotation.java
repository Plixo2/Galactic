package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public final class HIRCallNotation implements HIRExpression {
    private final Region region;
    private final HIRExpression object;
    private final List<HIRExpression> arguments;


}
