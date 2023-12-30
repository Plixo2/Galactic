package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record HIRBranch(Region region, HIRExpression condition, HIRExpression body,
                        @Nullable HIRExpression elseBody) implements HIRExpression {
}
