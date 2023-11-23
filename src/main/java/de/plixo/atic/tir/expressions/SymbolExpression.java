package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SymbolExpression extends Expression{

    @Getter
    private final String id;


    @Override
    public AType getType() {
        throw new NullPointerException("Not implemented");
    }
}
