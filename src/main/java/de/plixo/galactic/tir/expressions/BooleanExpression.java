package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record BooleanExpression(Region region, boolean value) implements Expression {
    @Override
    public Type getType(Context context) {
        return new PrimitiveType(PrimitiveType.StellaPrimitiveType.BOOLEAN);
    }
}
