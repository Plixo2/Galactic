package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class ClassExpression extends Expression {
    private final Region region;
    private final Class aClass;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
