package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class JVMField extends Expression {

    private final Expression object;
    private final Field field;
    private final Class<?> fieldClass;


    @Override
    public Type asAticType() {
        return JVMClassType.fromClass(fieldClass);
    }


    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(": \n");
        builder.append("field ").append(field.getName()).append("\n");
        builder.append(object);
        return builder.toString();
    }
}
