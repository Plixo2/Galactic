package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class BlockExpression extends Expression {

    private final Region region;
    private final List<Expression> expressions;

    @Override
    public Type getType(Context context) {
        if (expressions.isEmpty()) {
            return new VoidType();
        }
        return expressions.get(expressions.size() - 1).getType(context);
    }
}
