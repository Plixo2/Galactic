package de.plixo.galactic.hir.expressions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRDotNotation implements HIRExpression{

    @Getter
    private final HIRExpression object;
    @Getter
    private final String id;

}
