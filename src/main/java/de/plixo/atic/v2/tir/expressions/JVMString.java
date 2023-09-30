package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JVMString extends Expression {
    private final String string;

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(": \n");
        builder.append("string").append(string).append("\n");
        return builder.toString();
    }

    @Override
    public Type asAticType() {
        return JVMClassType.fromClass(String.class);
    }
}
