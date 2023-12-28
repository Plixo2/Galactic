package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record MethodCallExpression(Region region, de.plixo.galactic.tir.expressions.MethodCallExpression.MethodSource source,
                                   Method method, Type calledType, List<Expression> arguments)
        implements Expression {

    @Override
    public Type getType(Context context) {
        return method.returnType();
    }

    public sealed

    interface MethodSource permits StaticMethodExpression, GetMethodExpression {

    }
}
