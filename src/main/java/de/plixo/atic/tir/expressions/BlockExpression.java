package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BlockExpression extends Expression {

    @Getter
    private final List<Expression> expressions;
    @Override
    public Expression dotNotation(String id, Context context) {
        throw new NullPointerException("");
    }

    @Override
    public AType getType() {
        return new AVoid();
    }
}
