package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.Method;
import de.plixo.atic.v2.tir.type.MethodType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodExpression extends Expression {

    @Getter
    private final Method method;

    @Override
    public Type asAticType() {
        return new MethodType(method);
    }
}
