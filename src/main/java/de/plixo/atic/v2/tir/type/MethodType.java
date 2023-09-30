package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.Method;
import de.plixo.atic.v2.tir.expressions.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MethodType extends Type {

    @Getter
    private final Method method;

    @Override
    public Class<?> toJVMClass() {
        throw new NullPointerException("not supported");
    }

    @Override
    public Expression dotNotation(Expression expression, String id, Context context) {
        throw new NullPointerException("not supported");
    }

    @Override
    public Expression callNotation(Expression expression, List<HIRExpression> arguments,
                                   Context context) {
        throw new NullPointerException("not impl");
    }


}
