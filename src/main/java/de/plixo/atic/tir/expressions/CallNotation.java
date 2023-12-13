package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class CallNotation extends Expression {
    private final Expression object;
    private final List<Expression> arguments;

    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }
}
