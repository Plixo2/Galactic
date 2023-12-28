package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record BlockExpression(Region region, List<Expression> expressions) implements Expression {

    @Override
    public Type getType(Context context) {
        if (expressions.isEmpty()) {
            return new VoidType();
        }
        return expressions.getLast().getType(context);
    }
}
