package de.plixo.atic.tir.parsing;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import org.jetbrains.annotations.Nullable;

public class TIRTypeConversion {

    public static @Nullable Expression convert(Expression object, AType expected, Context context) {
        if (AType.isSame(expected, APrimitive.BOOLEAN)) {
            return asBoolean(object, context);
        }
        throw new NullPointerException("cant convert");
    }

    public static Expression asBoolean(Expression expression, Context context) {
        throw new NullPointerException("todo");
        //        var type = expression.getType();
//        if (AType.isAssignableFrom(APrimitive.BOOLEAN, type, context)) {
//            return expression;
//        }
//        if (type instanceof AClass aClass) {
//            if (aClass.path().asDotString().equals("java.lang.Boolean")) {
//                var dot = expression.dotNotation("booleanValue", context);
//                return dot.callExpression(new ArrayList<>(), context);
//            }
//        }
//        return null;
    }
}
