package de.plixo.galactic.hir.expressions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRString implements HIRExpression {

    @Getter
    private final String string;

}
