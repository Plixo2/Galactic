package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ConstructExpression extends Expression {

    @Getter
    private final AClass contructedClassType;
    @Getter
    private final AMethod constructor;
    @Getter
    private final List<Expression> expressions;

    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(contructedClassType, id, context);
    }

    @Override
    public AType getType() {
        return contructedClassType;
    }
}
