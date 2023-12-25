package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class CallNotation extends Expression {
    private final Region region;
    private final Expression object;
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }
}
