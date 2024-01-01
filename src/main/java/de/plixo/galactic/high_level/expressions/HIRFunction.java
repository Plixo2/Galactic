package de.plixo.galactic.high_level.expressions;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HIRFunction(Region region, List<HIRFunctionParameter> HIRParameters,
                          @Nullable HIRType returnType, HIRExpression expression,
                          @Nullable HIRType interfaceType) implements HIRExpression {


    public record HIRFunctionParameter(Region region, String name, @Nullable HIRType type) {
    }
}
