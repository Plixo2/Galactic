package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class GetFieldExpression extends Expression {
    private final Expression object;
    private final AField field;


    @Override
    public AType getType() {
        //TODO generics
        return field.type();
    }
}
