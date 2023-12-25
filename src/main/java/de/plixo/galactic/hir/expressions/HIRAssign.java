package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class HIRAssign implements HIRExpression {

    private final Region region;
    private final HIRExpression left;
    private final HIRExpression right;


}
