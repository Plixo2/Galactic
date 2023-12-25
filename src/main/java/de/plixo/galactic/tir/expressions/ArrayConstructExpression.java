package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.ArrayType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class ArrayConstructExpression extends Expression {
    private final Region region;
    private final Type elementType;

    private final List<Expression> values;

    @Override
    public Type getType(Context context) {
        return new ArrayType(elementType);
    }
}
