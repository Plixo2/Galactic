package de.plixo.galactic.hir.expressions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRIdentifier implements HIRExpression{
    @Getter
    private final String id;

}
