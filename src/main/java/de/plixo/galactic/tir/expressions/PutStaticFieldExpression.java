package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public final class PutStaticFieldExpression extends Expression {

    private final Region region;
    private final Field field;
    private final Expression value;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }

}
