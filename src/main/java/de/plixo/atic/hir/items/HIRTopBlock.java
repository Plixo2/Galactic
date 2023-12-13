package de.plixo.atic.hir.items;


import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public final class HIRTopBlock implements HIRItem {

    private final Region region;
    private final List<HIRExpression> expressions;
}
