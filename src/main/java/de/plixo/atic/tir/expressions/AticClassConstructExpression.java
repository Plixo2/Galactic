package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public final class AticClassConstructExpression extends Expression {

    private final AticClass constructType;

    @Getter
    private final List<Expression> arguments;

    @Override
    public AType getType() {
        //TODO generics?
        return constructType;
    }
}
