package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Path extends Expression {
    private final Region region;
    private final ObjectPath path;


    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
