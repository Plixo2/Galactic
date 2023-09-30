package de.plixo.atic.v2.tir.parsing;

import de.plixo.atic.v2.hir2.types.HIRArrayType;
import de.plixo.atic.v2.hir2.types.HIRClassType;
import de.plixo.atic.v2.hir2.types.HIRPrimitive;
import de.plixo.atic.v2.hir2.types.HIRType;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;

public class TIRTypeParsing {
    public static Type parse(HIRType type, Context context) {
        return switch (type) {
            case HIRArrayType hirArrayType -> parseArrayType(hirArrayType, context);
            case HIRClassType hirClassType -> parseClassType(hirClassType, context);
            case HIRPrimitive hirPrimitive -> new Primitive(hirPrimitive.primitiveType());
        };
    }

    private static Type parseArrayType(HIRArrayType arrayType, Context context) {
        var type = TIRTypeParsing.parse(arrayType.type(), context);
        if (type instanceof JVMClassType classType) {
            return JVMClassType.fromClass(classType.theClass().arrayType());
        } else if (type instanceof Primitive primitive) {
            return JVMClassType.fromClass(primitive.toJVMClass().arrayType());
        }else {
            throw new NullPointerException("not supported");
        }
    }

    private static Type parseClassType(HIRClassType classType, Context context) {
        var className = classType.path().asString();
        try {
            return JVMClassType.fromClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
