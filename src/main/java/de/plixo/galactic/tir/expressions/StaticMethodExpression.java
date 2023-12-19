package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.types.VoidType;
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
