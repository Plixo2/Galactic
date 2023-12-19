package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class SymbolExpression extends Expression {

    private final String id;


    @Override
    public Type getType(Context context) {
        throw new NullPointerException("Not implemented");
    }
}
