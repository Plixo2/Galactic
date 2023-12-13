package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class BranchExpression extends Expression {
    private final Expression condition;
    private final Expression then;
    private final @Nullable Expression elseExpression;


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
