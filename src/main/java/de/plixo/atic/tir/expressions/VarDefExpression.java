package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class VarDefExpression extends Expression {

    @Getter
    private final String name;

    @Getter
    private final AType hint;

    @Getter
    private final Expression expression;

    @Override
    public AType getType() {
        return new AVoid();
    }
}
