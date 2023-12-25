package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class BooleanExpression extends Expression {
    private final Region region;
    private final boolean value;

    @Override
    public Type getType(Context context) {
        return new PrimitiveType(PrimitiveType.StellaPrimitiveType.BOOLEAN);
    }
}
