package de.plixo.galactic.hir.items;

import de.plixo.galactic.hir.expressions.HIRExpression;
import de.plixo.galactic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRMethod {

    private final String methodName;
    private final List<HIRParameter> HIRParameters;
    private final HIRType returnType;
    private final HIRExpression expression;


    @Getter
    @RequiredArgsConstructor
    public static class HIRParameter {
        private final String name;
        private final HIRType type;

    }
}
