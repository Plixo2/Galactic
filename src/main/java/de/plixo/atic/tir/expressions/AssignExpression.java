package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class AssignExpression extends Expression{

    private final Expression left;
    private final Expression right;

    @Override
    public AType getType(Context context) {
        return new AVoid();
    }
}
