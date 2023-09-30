package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.List;

@RequiredArgsConstructor
public class JVMConstructExpression extends Expression{

    @Getter
    private final Constructor<?> constructor;

    @Getter
    private final JVMClassType type;

    @Getter
    private final List<ConstructParameter> parameters;

    @Override
    public Type asAticType() {
        return type;
    }

    @RequiredArgsConstructor
    public static class ConstructParameter {
        @Getter
        private final String name;
        @Getter
        private final Expression value;
    }
}
