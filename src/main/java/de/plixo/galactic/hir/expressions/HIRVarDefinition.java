package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public final class HIRVarDefinition implements HIRExpression {
    private final Region region;
    private final String name;
    private final @Nullable HIRType type;
    private final HIRExpression value;

}
