package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class MethodCallExpression extends Expression{

    private final MethodSource source;
    private final AMethod method;
    private final List<Expression> arguments;

    @Override
    public AType getType() {
        return method.returnType();
    }

    public sealed interface MethodSource permits StaticMethodExpression, GetMethodExpression {

    }
}
