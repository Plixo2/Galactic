package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class InstanceCreationExpression extends Expression{

    private final AMethod constructor;
    private final AType type;
    private final List<Expression> expressions;

    @Override
    public AType getType() {
        return type;
    }
}
