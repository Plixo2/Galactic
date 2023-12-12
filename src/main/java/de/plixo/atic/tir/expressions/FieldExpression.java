package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class FieldExpression extends Expression {
    private final Expression object;
    private final AField field;


    @Override
    public AType getType(Context context) {
        //TODO generics
        return field.type();
    }
}
