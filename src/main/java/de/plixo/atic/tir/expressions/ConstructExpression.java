package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ConstructExpression extends Expression {

    @Getter
    private final AType constructType;

    @Getter
    private final List<Expression> arguments;

    @Override
    public AType getType() {
        throw new NullPointerException("to compute");
    }
}
