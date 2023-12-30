package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public record StaticMethodExpression(Region region, MethodOwner owner, MethodCollection methods)
        implements Expression, MethodCallExpression.MethodSource {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
