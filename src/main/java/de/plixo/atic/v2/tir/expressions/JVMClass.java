package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor
public class JVMClass extends Expression{

    private final Class<?> theClass;


    @Override
    public Type asAticType() {
        return JVMClassType.fromClass(theClass);
    }


    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(": \n");
        builder.append(theClass);
        return builder.toString();
    }
}
