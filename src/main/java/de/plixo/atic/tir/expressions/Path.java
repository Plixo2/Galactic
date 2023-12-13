package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.Context;
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
