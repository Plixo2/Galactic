package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.types.Type;
import de.plixo.galactic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ConstructExpression extends Expression {

    @Getter
    private final Type constructType;

    @Getter
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        return constructType;
    }
}
