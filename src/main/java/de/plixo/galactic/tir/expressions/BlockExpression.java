package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import de.plixo.galactic.tir.Context;
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
