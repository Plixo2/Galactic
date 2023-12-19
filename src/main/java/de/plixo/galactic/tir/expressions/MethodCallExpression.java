package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class MethodCallExpression extends Expression{

    private final MethodSource source;
    private final Method method;
    private final Type calledType;
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        return method.returnType();
    }

    public sealed interface MethodSource permits StaticMethodExpression, GetMethodExpression {

    }
}
