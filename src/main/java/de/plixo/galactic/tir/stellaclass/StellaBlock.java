package de.plixo.galactic.tir.stellaclass;

import de.plixo.galactic.hir.expressions.HIRExpression;
import de.plixo.galactic.tir.expressions.Expression;
import de.plixo.galactic.tir.path.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


/**
 * Static code block
 */
@RequiredArgsConstructor
@Getter
public class StellaBlock {
    private final Unit unit;
    private final HIRExpression hirBlock;
    @Setter
    private @Nullable Expression expression;
}
