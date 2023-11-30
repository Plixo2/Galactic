package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AField;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StaticFieldExpression extends Expression {
    @Getter
    private final AticClass aClass;
    @Getter
    private final AField field;


    @Override
    public AType getType() {
        return field.type();
    }
}
