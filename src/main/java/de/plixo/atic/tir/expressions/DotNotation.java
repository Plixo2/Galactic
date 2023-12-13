package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public final class DotNotation extends Expression {
    private final Expression object;
    private final String id;

    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }


}
