package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.sub.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class MethodCallExpression extends Expression{

    private final MethodSource source;
    private final Method method;
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        return method.returnType();
    }

    public sealed interface MethodSource permits StaticMethodExpression, GetMethodExpression {

    }
}
