package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class AssignExpression extends Expression {
    private final Region region;
    private final Expression left;
    private final Expression right;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
