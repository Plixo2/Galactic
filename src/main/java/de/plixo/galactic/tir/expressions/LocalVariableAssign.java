package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public final class LocalVariableAssign extends Expression {

    private final Region region;
    private final @Nullable Scope.Variable variable;
    private final Expression expression;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
