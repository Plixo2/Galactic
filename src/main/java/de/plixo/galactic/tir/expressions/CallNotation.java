package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record CallNotation(Region region, Expression object, List<Expression> arguments) implements Expression {
    @Override
    public Type getType(Context context) {
        throw new NullPointerException("not computed yet");
    }
}
