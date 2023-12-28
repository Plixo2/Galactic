package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record StaticFieldExpression(Region region, Class aClass, Field field) implements Expression {
    @Override
    public Type getType(Context context) {
        return field.type();
    }
}
