package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.Context;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Modifier;

@RequiredArgsConstructor
public final class ClassExpression extends Expression {
    private final AClass aClass;

    @Override
    public AType getType() {
        return new AVoid();
    }
}
