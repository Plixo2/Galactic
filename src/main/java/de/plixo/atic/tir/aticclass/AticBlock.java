package de.plixo.atic.tir.aticclass;

import de.plixo.atic.hir.expressions.HIRBlock;
import de.plixo.atic.tir.expressions.BlockExpression;
import de.plixo.atic.tir.path.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public class AticBlock {
    private final Unit unit;
    private final HIRBlock hirBlock;
    @Setter
    private @Nullable BlockExpression expression;
}
