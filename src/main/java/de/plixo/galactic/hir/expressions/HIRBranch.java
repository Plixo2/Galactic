package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

public record HIRBranch(Region region, HIRExpression condition, HIRExpression body,
                        @Nullable HIRExpression elseBody) implements HIRExpression {
}
