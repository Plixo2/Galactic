package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.Type;
import de.plixo.atic.tir.Context;
import de.plixo.atic.types.JVMClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StringExpression extends Expression {

    private final String value;

    @Override
    public Type getType(Context context) {
        return new JVMClass("java.lang.String");
    }
}
