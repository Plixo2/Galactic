package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record HIRVarDefinition(Region region, String name, @Nullable HIRType type,
                               HIRExpression value) implements HIRExpression {
}
