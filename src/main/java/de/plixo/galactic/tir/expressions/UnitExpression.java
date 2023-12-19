package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class UnitExpression extends Expression{
    private final Unit unit;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
