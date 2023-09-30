package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JVMObject extends Expression {

    @Getter
    private final Expression object;
    @Getter
    private final Class<?> theClass;

    @Override
    public Type asAticType() {
        return JVMClassType.fromClass(theClass);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(": \n");
        builder.append(theClass).append("\n");
        builder.append(object);
        return builder.toString();
    }
}
