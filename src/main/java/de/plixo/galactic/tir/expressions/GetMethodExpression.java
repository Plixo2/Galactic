package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class GetMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final Region region;
    private final Expression object;
    private final MethodCollection methods;


    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
