package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableDefinitionExpression extends Expression {

    @Getter
    private final Context.Variable variable;

    @Getter
    private final AType hint;

    @Getter
    private final Expression expression;

    @Override
    public AType getType() {
        return new AVoid();
    }
}
