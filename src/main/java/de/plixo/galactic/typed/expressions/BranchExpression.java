package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import org.jetbrains.annotations.Nullable;

public record BranchExpression(Region region, Expression condition, Expression then,
                               @Nullable Expression elseExpression) implements Expression {
    @Override
    public Type getType(Context context) {
        if (elseExpression == null) {
            return new VoidType();
        }
        var left = then.getType(context);
        var right = elseExpression.getType(context);
        if (!Type.isAssignableFrom(left, right, context)) {
            return new VoidType();
        }
        return left;
    }
}
