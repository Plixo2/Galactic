package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class DotNotation extends Expression {
    private final Expression object;
    private final String id;

    @Override
    public AType getType() {
        throw new NullPointerException("not computed yet");
    }
}
