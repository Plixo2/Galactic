package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.items.HIRParameter;
import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRFunction implements HIRExpression {
    private final Region region;
    private final List<HIRParameter> HIRParameters;
    private final HIRType returnType;
    private final HIRExpression expression;
}
