package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import de.plixo.atic.tir.MethodCollection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StaticMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final Class aClass;
    private final MethodCollection methods;

    @Override
    public Type getType(Context context) {
        //TODO caller has to check this type
        throw new NullPointerException("caller has to check this type, internal error");
    }
}
