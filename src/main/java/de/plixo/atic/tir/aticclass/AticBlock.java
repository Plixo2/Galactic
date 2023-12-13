package de.plixo.atic.tir.aticclass;

import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.tir.path.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public class AticBlock {
    private final Unit unit;
    private final HIRExpression hirBlock;
    @Setter
    private @Nullable Expression expression;
}
