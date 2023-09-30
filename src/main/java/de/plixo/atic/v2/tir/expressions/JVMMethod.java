package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.JVMMethodType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor
public class JVMMethod extends Expression {

    private final Expression object;
    private final List<Method> methods;

    @Override
    public Type asAticType() {
        return new JVMMethodType(methods);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(": \n");
        builder.append("method ").append(methods).append("\n");
        builder.append(object);
        return builder.toString();
    }
}
