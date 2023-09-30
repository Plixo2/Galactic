package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BranchExpression extends Expression {

    private final Expression condition;
    private final Expression body;
    @Override
    public Type asAticType() {
        return Primitive.VOID;
    }
}
