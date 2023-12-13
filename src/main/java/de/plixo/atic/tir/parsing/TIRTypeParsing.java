package de.plixo.atic.tir.parsing;

import de.plixo.atic.types.ArrayType;
import de.plixo.atic.types.PrimitiveType;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import de.plixo.atic.hir.types.HIRArrayType;
import de.plixo.atic.hir.types.HIRClassType;
import de.plixo.atic.hir.types.HIRPrimitive;
import de.plixo.atic.hir.types.HIRType;
import de.plixo.atic.tir.Context;

import java.util.Objects;

/**
 * Parses a {@link HIRType} to a {@link Type}
 */
public class TIRTypeParsing {
    public static Type parse(HIRType type, Context context) {
        return switch (type) {
            case HIRArrayType hirArrayType -> parseArrayType(hirArrayType, context);
            case HIRClassType hirClassType -> parseClassType(hirClassType, context);
            case HIRPrimitive hirPrimitive -> parsePrimitive(hirPrimitive, context);
        };
    }

    public static Type parsePrimitive(HIRPrimitive hirPrimitive, Context context) {
        var primitiveType = hirPrimitive.primitiveType();
        var aPrimitiveType = PrimitiveType.APrimitiveType.fromHIR(primitiveType);
        if (aPrimitiveType == null) {
            return new VoidType();
        }
        return new PrimitiveType(aPrimitiveType);
    }

    private static Type parseArrayType(HIRArrayType arrayType, Context context) {
        return new ArrayType(parse(arrayType.type(), context));
    }

    private static Type parseClassType(HIRClassType classType, Context context) {
        var className = classType.path();
        return Objects.requireNonNull(context.getClass(className), "cant find type " + className);
    }
}

