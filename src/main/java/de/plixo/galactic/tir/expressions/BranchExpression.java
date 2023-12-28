package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
