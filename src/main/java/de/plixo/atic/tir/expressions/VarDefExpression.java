package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public final class VarDefExpression extends Expression {
    private final String name;
    private final @Nullable Type hint;
    private final Expression expression;
    private final @Nullable Scope.Variable variable;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
