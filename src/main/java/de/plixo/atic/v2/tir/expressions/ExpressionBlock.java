package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ExpressionBlock extends Expression {

    @Getter
    private final List<Expression> expressions;

    @Override
    public Type asAticType() {
        throw new NullPointerException("not supported yet");
    }
}
