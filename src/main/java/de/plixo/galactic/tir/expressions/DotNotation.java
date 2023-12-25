package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public final class DotNotation extends Expression {
    private final Region region;
    private final Expression object;
    private final String id;

    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }


}
