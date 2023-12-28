package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record HIRVarDefinition(Region region, String name, @Nullable HIRType type,
                               HIRExpression value) implements HIRExpression {
}
