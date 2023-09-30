package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.Expression;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Type {
    public abstract Class<?> toJVMClass();
    public abstract Expression dotNotation(Expression expression, String id, Context context);
    public abstract Expression callNotation(Expression expression, List<HIRExpression> arguments, Context context);

    public @Nullable Type getSuperType() {
        return null;
    }
}
