package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class GetMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final Expression object;
    private final MethodCollection methods;


    @Override
    public AType getType() {
        //TODO caller has to check for this case
        throw new NullPointerException("caller has to check for this case");
    }
}
