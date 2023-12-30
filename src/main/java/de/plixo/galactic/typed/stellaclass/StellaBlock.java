package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.high_level.expressions.HIRExpression;
import de.plixo.galactic.typed.expressions.Expression;
import de.plixo.galactic.typed.path.Unit;
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
