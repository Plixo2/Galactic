package de.plixo.galactic.high_level.items;


import de.plixo.galactic.high_level.expressions.HIRExpression;
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
