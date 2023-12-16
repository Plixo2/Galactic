package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StaticClassExpression extends Expression{
    private final Class theClass;

    @Override
    public Type getType(Context context) {
        //TODO return the class type here, like in Java some.class
        throw new NullPointerException("AticClassExpression has no type");
    }
}
