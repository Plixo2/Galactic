package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.high_level.types.HIRArrayType;
import de.plixo.galactic.high_level.types.HIRClassType;
import de.plixo.galactic.high_level.types.HIRPrimitive;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.ArrayType;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

import static de.plixo.galactic.exception.FlairKind.UNKNOWN_TYPE;

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
        var aPrimitiveType = PrimitiveType.StellaPrimitiveType.fromHIR(primitiveType);
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
        var foundClass = context.getClass(className);
        if (foundClass == null) {
            throw new FlairCheckException(classType.region(), UNKNOWN_TYPE,
                    STR."cant find type \{className}");
        }
        return foundClass;
    }
}

