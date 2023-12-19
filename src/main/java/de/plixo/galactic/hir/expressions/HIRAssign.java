package de.plixo.galactic.hir.expressions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class HIRAssign implements HIRExpression{

    private final HIRExpression left;
    private final HIRExpression right;


}
