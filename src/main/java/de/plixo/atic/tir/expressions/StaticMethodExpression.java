package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.aticclass.MethodOwner;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StaticMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final MethodOwner owner;
    private final MethodCollection methods;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
