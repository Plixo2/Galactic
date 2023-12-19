package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StaticClassExpression extends Expression{
    private final Class theClass;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
