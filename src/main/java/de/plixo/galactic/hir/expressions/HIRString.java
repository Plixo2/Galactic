package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class HIRString implements HIRExpression {
    private final Region region;
    private final String string;

}
