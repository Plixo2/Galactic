package de.plixo.atic.tir.parsing;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TypeConversion {

    public static @Nullable Expression convert(Expression object, AType expected, Context context) {
        if (AType.isSame(expected, APrimitive.BOOLEAN)) {
            return asBoolean(object, context);
        }
        throw new NullPointerException("cant convert");
    }

    public static Expression asBoolean(Expression expression, Context context) {
        var type = expression.getType();
        if (AType.isAssignableFrom(APrimitive.BOOLEAN, type, context)) {
            return expression;
        }
        if (type instanceof AClass aClass) {
            if (aClass.path().asDotString().equals("java.lang.Boolean")) {
                var dot = expression.dotNotation("booleanValue", context);
                return dot.callExpression(new ArrayList<>(), context);
            }
        }
        return null;
    }
}
