package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Type;

import java.util.List;

public record MethodCallExpression(Region region, MethodSource source, Method method,
                                   Type calledType, List<Expression> arguments)
        implements Expression {

    @Override
    public Type getType(Context context) {
        return method.returnType();
    }

    public sealed interface MethodSource permits StaticMethodExpression, GetMethodExpression {
        MethodCollection methods();
        Type getCallType(Context context);
    }
}
