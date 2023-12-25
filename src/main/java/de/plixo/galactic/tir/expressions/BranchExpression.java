package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class BranchExpression extends Expression {
    private final Region region;
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
