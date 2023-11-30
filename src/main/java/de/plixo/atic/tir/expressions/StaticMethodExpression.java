package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.tir.MethodCollection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StaticMethodExpression extends Expression
        implements MethodCallExpression.MethodSource {
    private final AticClass aClass;
    private final MethodCollection methods;

    @Override
    public AType getType() {
        //TODO caller has to check this type
        throw new NullPointerException("caller has to check this type, internal error");
    }
}
