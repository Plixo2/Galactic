package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public record DotNotation(Region region, Expression object, String id) implements Expression {
    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }


}
