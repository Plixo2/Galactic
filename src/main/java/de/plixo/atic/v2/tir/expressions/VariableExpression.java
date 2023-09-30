package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableExpression extends Expression {

    @Getter
    private final Context.Variable variable;

    @Override
    public Type asAticType() {
        return variable.type();
    }
}
