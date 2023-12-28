package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;

import java.util.List;

public record HIRConstruct(Region region, HIRType hirType, List<ConstructParam> parameters)
        implements HIRExpression {
    public record ConstructParam(Region region, String name, HIRExpression value) {
    }
}
