package de.plixo.galactic.hir.expressions;

import de.plixo.galactic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRConstruct implements HIRExpression {

    @Getter
    private final HIRType hirType;
    @Getter
    private final List<ConstructParam> parameters;



    @RequiredArgsConstructor
    public static class ConstructParam {
        @Getter
        private final String name;
        @Getter
        private final HIRExpression value;

    }
}
