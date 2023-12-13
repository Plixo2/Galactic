package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class BlockExpression extends Expression {

    @Getter
    private final List<Expression> expressions;

    @Override
    public Type getType(Context context) {
        if (expressions.isEmpty()) {
            return new VoidType();
        }
        return expressions.get(expressions.size() - 1).getType(context);
    }
}
