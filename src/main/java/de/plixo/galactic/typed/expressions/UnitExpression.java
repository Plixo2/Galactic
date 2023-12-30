package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public record UnitExpression(Region region, Unit unit) implements Expression {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
