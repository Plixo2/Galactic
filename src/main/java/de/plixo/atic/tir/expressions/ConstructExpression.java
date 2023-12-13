package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Type;
import de.plixo.atic.tir.Context;
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
