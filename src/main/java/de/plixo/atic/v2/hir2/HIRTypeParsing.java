package de.plixo.atic.v2.hir2;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.v2.hir2.types.HIRArrayType;
import de.plixo.atic.v2.hir2.types.HIRClassType;
import de.plixo.atic.v2.hir2.types.HIRPrimitive;
import de.plixo.atic.v2.hir2.types.HIRType;
import de.plixo.atic.v2.hir2.utils.DotWordChain;
import de.plixo.atic.v2.tir.type.Primitive;

import java.util.Objects;

public class HIRTypeParsing {
    public static HIRType parse(Node node) {
        node.assertType("type");
        if (node.has("arrayType")) {
            return parseArrayType(node.get("arrayType"));
        } else if (node.has("classType")) {
            return parseClassType(node.get("classType"));
        } else if (node.has("primitive")) {
            return parsePrimitiveType(node.get("primitive"));
        }
        throw new NullPointerException("unknown type " + node);
    }

    private static HIRPrimitive parsePrimitiveType(Node node) {
        var name = Objects.requireNonNull(node.child().record()).data();
        node.assertType("primitive");
        var primitive = switch (name) {
            case "int" -> Primitive.PrimitiveType.INT;
            case "byte" -> Primitive.PrimitiveType.BYTE;
            case "short" -> Primitive.PrimitiveType.SHORT;
            case "long" -> Primitive.PrimitiveType.LONG;
            case "float" -> Primitive.PrimitiveType.FLOAT;
            case "double" -> Primitive.PrimitiveType.DOUBLE;
            case "boolean" -> Primitive.PrimitiveType.BOOLEAN;
            case "char" -> Primitive.PrimitiveType.CHAR;
            case null, default ->
                    throw new IllegalStateException("Unexpected value: " + name);
        };
        return new HIRPrimitive(node.region(), primitive);
    }

    private static HIRArrayType parseArrayType(Node node) {
        node.assertType("arrayType");
        var type = HIRTypeParsing.parse(node.get("type"));
        return new HIRArrayType(node.region(), type);
    }

    private static HIRClassType parseClassType(Node node) {
        node.assertType("classType");
        var dotWordChain = new DotWordChain(node.get("dotWordChain"));
        return new HIRClassType(node.region(), dotWordChain);
    }
}
