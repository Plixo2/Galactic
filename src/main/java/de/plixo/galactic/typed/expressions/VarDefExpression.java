package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import org.jetbrains.annotations.Nullable;

public record VarDefExpression(Region region, String name, @Nullable Type hint,
                               Expression expression, @Nullable Scope.Variable variable)
        implements Expression {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
