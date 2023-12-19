package de.plixo.galactic.hir.expressions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRBlock implements HIRExpression {

    @Getter
    private final List<HIRExpression> expressions;

}
