package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class SymbolExpression extends Expression {

    private final String id;


    @Override
    public AType getType(Context context) {
        throw new NullPointerException("Not implemented");
    }
}
