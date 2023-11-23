package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Converter;
import de.plixo.atic.types.classes.JVMClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StringExpression extends Expression {

    private final String value;

    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(getType(), id, context);
    }

    @Override
    public AType getType() {
        return new JVMClass("java.lang.String");
    }
}
