package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.Type;

import java.util.List;

public abstract class Expression {
    public Expression callNotation(List<HIRExpression> arguments, Context context) {
        return asAticType().callNotation(this,arguments, context);
    }
    public Expression dotNotation(String id, Context context) {
        return asAticType().dotNotation(this,id, context);
    }

    public abstract Type asAticType();

}
