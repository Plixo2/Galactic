package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record StellaClassConstructExpression(Region region, Class constructType,
                                             @Getter List<Expression> arguments) implements Expression {

    @Override
    public Type getType(Context context) {
        return constructType;
    }
}
