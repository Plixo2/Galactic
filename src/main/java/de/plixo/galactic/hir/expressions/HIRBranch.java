package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public final class HIRBranch implements HIRExpression {
    private final Region region;
    private final HIRExpression condition;
    private final HIRExpression body;

    private final @Nullable HIRExpression elseBody;
}
