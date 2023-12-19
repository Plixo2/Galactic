package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Path extends Expression {
    private final ObjectPath path;


    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
