package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class InstanceCreationExpression extends Expression {

    private final Region region;
    private final Method constructor;
    private final Class type;
    private final List<Expression> expressions;

    @Override
    public Type getType(Context context) {
        return type;
    }
}
