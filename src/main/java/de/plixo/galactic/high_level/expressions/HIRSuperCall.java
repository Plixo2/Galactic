package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HIRSuperCall(Region region, @Nullable HIRType superType, @Nullable String method,
                           List<HIRExpression> arguments) implements HIRExpression {

}
