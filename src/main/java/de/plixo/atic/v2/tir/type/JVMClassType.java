package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRConstruct;
import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.Expression;
import de.plixo.atic.v2.tir.expressions.JVMConstructExpression;
import de.plixo.atic.v2.tir.expressions.JVMField;
import de.plixo.atic.v2.tir.expressions.JVMMethod;
import de.plixo.atic.v2.tir.parsing.TIRExpressionParsing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JVMClassType extends Type {

    public static Type fromClass(Class<?> theClass) {
        var primitive = Primitive.toPrimitive(theClass);
        if (primitive != null) {
            return primitive;
        }

        return new JVMClassType(theClass);
    }

    @Getter
    private final Class<?> theClass;

    @Override
    public Class<?> toJVMClass() {
        return theClass;
    }

    @Override
    public Expression dotNotation(Expression expression, String id, Context context) {
        var field = getField(id);
        if (field != null) {
            return new JVMField(expression, field, field.getType());
        }
        var method = getMethod(id);
        if (!method.isEmpty()) {
            return new JVMMethod(expression, method);
        }

        throw new NullPointerException("no such method or field: " + id + " in " + theClass);
    }

    @Override
    public Expression callNotation(Expression expression, List<HIRExpression> arguments,
                                   Context context) {
        throw new NullPointerException("not impl");
    }

    private @Nullable Field getField(String name) {
        try {
            return theClass.getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private List<Method> getMethod(String name) {
        var list = new ArrayList<Method>();
        for (Method method : theClass.getMethods()) {
            if (method.getName().equals(name)) {
                list.add(method);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "JVMClassType{" + "theClass=" + theClass + '}';
    }

    @Override
    public @Nullable Type getSuperType() {
        var superclass = theClass.getSuperclass();
        if (superclass == null) {
            return null;
        }
        return new JVMClassType(superclass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JVMClassType that = (JVMClassType) o;
        return Objects.equals(theClass, that.theClass);
    }

    public Expression construct(HIRConstruct construct, Context context) {
        var expressions = construct.parameters().stream().map(constructParam -> {
            var childContext = context.childContext();
            var parse = TIRExpressionParsing.parse(constructParam.value(), childContext);
            return new JVMConstructExpression.ConstructParameter(constructParam.name(), parse);
        }).toList();
        var jvmClasses =
                expressions.stream().map(expression -> expression.value().asAticType().toJVMClass())
                        .toList();
        Constructor<?> usedConstructor = null;
        for (var constructor : theClass.getConstructors()) {
            if (isConstructorCallable(constructor, jvmClasses)) {
                usedConstructor = constructor;
                break;
            }
        }
        if (usedConstructor == null) {
            throw new NullPointerException("cant find constructor with types " + jvmClasses);
        }

        return new JVMConstructExpression(usedConstructor, this, expressions);
    }

    private static boolean isConstructorCallable(Constructor<?> constructor,
                                                 List<? extends Class<?>> arguments) {
        var parameterTypes = constructor.getParameterTypes();

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
