package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record GetMethodExpression(Region region, Expression object, MethodCollection methods)
        implements Expression, MethodCallExpression.MethodSource {
    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
