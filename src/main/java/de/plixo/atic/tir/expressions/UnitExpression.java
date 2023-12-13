package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class UnitExpression extends Expression{
    private final Unit unit;

    @Override
    public Type getType(Context context) {
        //TODO throw sensible error here, or return void
        throw new NullPointerException("UnitExpression has no type");
    }
}
