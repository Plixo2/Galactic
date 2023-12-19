package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import de.plixo.galactic.tir.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassExpression extends Expression {
    private final Class aClass;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
