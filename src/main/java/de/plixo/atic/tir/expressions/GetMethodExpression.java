package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class GetMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final Expression object;
    private final MethodCollection methods;


    @Override
    public Type getType(Context context) {
        //TODO caller has to check for this case
        throw new NullPointerException("caller has to check for this case");
    }
}
