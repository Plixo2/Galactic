package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public final class HIRVarDefinition implements HIRExpression{

    @Getter
    private final String name;
    @Getter
    private final @Nullable HIRType type;
    @Getter
    private final HIRExpression value;

}
