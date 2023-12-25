package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class HIRBlock implements HIRExpression {
    private final Region region;
    private final List<HIRExpression> expressions;

}
