package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableExpression extends Expression{

    @Getter
    private final Context.Variable variable;


    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(variable.type(),id,context);
    }

    @Override
    public AType getType() {
        return variable.type();
    }
}
