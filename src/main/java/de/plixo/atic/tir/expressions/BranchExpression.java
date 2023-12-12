package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
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
    public AType getType(Context context) {
        if (elseExpression == null) {
            return new AVoid();
        }
        var left = then.getType(context);
        var right = elseExpression.getType(context);
        if (!AType.isAssignableFrom(left, right, context)) {
            return new AVoid();
        }
        return left;
    }
}
