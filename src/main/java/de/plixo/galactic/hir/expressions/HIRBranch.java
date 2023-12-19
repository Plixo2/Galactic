package de.plixo.galactic.hir.expressions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class HIRBranch implements HIRExpression {
    @Getter
    private final HIRExpression condition;
    @Getter
    private final HIRExpression body;

    @Getter
    private final @Nullable HIRExpression elseBody;
}
