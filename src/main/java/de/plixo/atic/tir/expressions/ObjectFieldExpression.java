package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AField;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ObjectFieldExpression extends Expression {

    @Getter
    private final Expression object;
    @Getter
    private final AField field;

    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(getType(),id,context);
    }

    @Override
    public AType getType() {
        return field.type;
    }
}
