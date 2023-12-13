package de.plixo.atic.hir;

import de.plixo.atic.common.PrimitiveType;
import de.plixo.atic.hir.types.HIRArrayType;
import de.plixo.atic.hir.types.HIRPrimitive;
import de.plixo.atic.hir.utils.DotWordChain;
import de.plixo.atic.parsing.Node;
import de.plixo.atic.hir.types.HIRClassType;
import de.plixo.atic.hir.types.HIRType;

import java.util.Objects;

/**
 * Parses a type node to a HIRType
 */
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
        var name = Objects.requireNonNull(node.child().record()).literal();
        node.assertType("primitive");
        var primitive = switch (name) {
            case "int" -> PrimitiveType.INT;
            case "byte" -> PrimitiveType.BYTE;
            case "short" -> PrimitiveType.SHORT;
            case "long" -> PrimitiveType.LONG;
            case "float" -> PrimitiveType.FLOAT;
            case "double" -> PrimitiveType.DOUBLE;
            case "boolean" -> PrimitiveType.BOOLEAN;
            case "char" -> PrimitiveType.CHAR;
            case "void" -> PrimitiveType.VOID;
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
        return new HIRClassType(dotWordChain.asObjectPath());
    }
}
