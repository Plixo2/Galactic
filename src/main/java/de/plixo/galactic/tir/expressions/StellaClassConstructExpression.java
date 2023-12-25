package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class StellaClassConstructExpression extends Expression {

    private final Region region;
    private final Class constructType;

    @Getter
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        return constructType;
    }
}
