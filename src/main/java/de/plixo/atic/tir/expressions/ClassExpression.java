package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import de.plixo.atic.tir.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassExpression extends Expression {
    private final Class aClass;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
