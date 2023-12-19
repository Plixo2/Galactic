package de.plixo.galactic.hir.items;


import de.plixo.galactic.hir.expressions.HIRExpression;
import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public final class HIRTopBlock implements HIRItem {

    private final Region region;
    private final List<HIRExpression> expressions;
}
