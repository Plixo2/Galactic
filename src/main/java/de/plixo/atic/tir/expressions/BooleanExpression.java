package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanExpression extends Expression{

    @Getter
    private final boolean values;
    @Override
    public AType getType() {
        return new APrimitive(APrimitive.APrimitiveType.BOOLEAN);
    }
}
