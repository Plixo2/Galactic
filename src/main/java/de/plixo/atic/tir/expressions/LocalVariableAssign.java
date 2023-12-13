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
public final class LocalVariableAssign extends Expression {

    private final @Nullable Scope.Variable variable;
    private final Expression expression;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
