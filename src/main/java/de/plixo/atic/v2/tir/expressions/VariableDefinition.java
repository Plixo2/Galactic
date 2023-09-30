package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableDefinition extends Expression {

    @Getter
    private final Context.Variable variable;
    @Getter
    private final Expression expression;

    @Override
    public Type asAticType() {
        throw new NullPointerException("not supported");
    }
}
