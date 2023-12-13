package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class InstanceCreationExpression extends Expression{

    private final Method constructor;
    private final Type type;
    private final List<Expression> expressions;

    @Override
    public Type getType(Context context) {
        return type;
    }
}
