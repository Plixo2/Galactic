package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.Expression;
import de.plixo.atic.v2.tir.expressions.JVMObject;
import de.plixo.atic.v2.tir.parsing.TIRExpressionParsing;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class JVMMethodType extends Type {

    private final List<Method> methods;

    @Override
    public Class<?> toJVMClass() {
        throw new NullPointerException("cant convert");
    }

    @Override
    public Expression dotNotation(Expression expression, String id, Context context) {
        throw new NullPointerException("not supported");
    }

    @Override
    public Expression callNotation(Expression expression, List<HIRExpression> arguments,
                                   Context context) {
        var classArguments =
                arguments.stream().map(expr -> TIRExpressionParsing.parse(expr, context))
                        .map(expr -> expr.asAticType().toJVMClass()).toList();
        Method calledMethod = null;
        for (var method : methods) {
            if (isMethodCallable(method, classArguments)) {
                calledMethod = method;
            }
        }

        if (calledMethod == null) {
            throw new NullPointerException(
                    "cant find function with signature of " + classArguments + " in " + methods);
        }
        var returnType = calledMethod.getReturnType();
        return new JVMObject(expression, returnType);
    }

    @Override
    public String toString() {
        return "JVMFunctionType{" + "methods=" + methods + '}';
    }

    private static boolean isMethodCallable(Method method, List<? extends Class<?>> arguments) {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (arguments.size() != parameterTypes.length) {
            return false; // The number of arguments does not match the number of parameters.
        }

        for (int i = 0; i < arguments.size(); i++) {
            if (!parameterTypes[i].isAssignableFrom(arguments.get(i))) {
                return false; // Argument type is not assignable to parameter type.
            }
        }

        return true;
    }

}
