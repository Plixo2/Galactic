package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class HIRIdentifier implements HIRExpression {
    private final Region region;
    private final String id;
}
