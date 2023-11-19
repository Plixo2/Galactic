package de.plixo.atic.tir.parsing;

import de.plixo.atic.types.AArray;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.hir.types.HIRArrayType;
import de.plixo.atic.hir.types.HIRClassType;
import de.plixo.atic.hir.types.HIRPrimitive;
import de.plixo.atic.hir.types.HIRType;
import de.plixo.atic.tir.Context;

import java.util.Objects;

public class TIRTypeParsing {
    public static AType parse(HIRType type, Context context) {
        return switch (type) {
            case HIRArrayType hirArrayType -> parseArrayType(hirArrayType, context);
            case HIRClassType hirClassType -> parseClassType(hirClassType, context);
            case HIRPrimitive hirPrimitive -> parsePrimitive(hirPrimitive, context);
        };
    }

    public static AType parsePrimitive(HIRPrimitive hirPrimitive, Context context) {
        var primitiveType = hirPrimitive.primitiveType();
        var aPrimitiveType = APrimitive.APrimitiveType.fromHIR(primitiveType);
        if (aPrimitiveType == null) {
            return new AVoid();
        }
        return new APrimitive(aPrimitiveType);
    }

    private static AType parseArrayType(HIRArrayType arrayType, Context context) {
        return new AArray(parse(arrayType.type(), context));
    }

    private static AType parseClassType(HIRClassType classType, Context context) {
        var className = classType.path();
        return Objects.requireNonNull(context.getClass(className), "cant find type " + className);
    }
}

