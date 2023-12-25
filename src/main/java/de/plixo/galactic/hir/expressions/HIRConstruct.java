package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class HIRConstruct implements HIRExpression {
    private final Region region;
    private final HIRType hirType;
    private final List<ConstructParam> parameters;


    @RequiredArgsConstructor
    @Getter
    public static class ConstructParam {
        private final Region region;
        private final String name;
        private final HIRExpression value;
    }
}
