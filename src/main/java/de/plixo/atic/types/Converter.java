package de.plixo.atic.types;

import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.types.classes.JVMClass;
import de.plixo.atic.types.sub.AField;
import de.plixo.atic.types.sub.AMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

public class Converter {



    @SneakyThrows
    public static AType getType(Type type) {
        return switch (type.getSort()) {
            case Type.VOID -> new AVoid();
            case Type.BOOLEAN -> new APrimitive(APrimitive.APrimitiveType.BOOLEAN);
            case Type.CHAR -> new APrimitive(APrimitive.APrimitiveType.CHAR);
            case Type.BYTE -> new APrimitive(APrimitive.APrimitiveType.BYTE);
            case Type.SHORT -> new APrimitive(APrimitive.APrimitiveType.SHORT);
            case Type.INT -> new APrimitive(APrimitive.APrimitiveType.INT);
            case Type.FLOAT -> new APrimitive(APrimitive.APrimitiveType.FLOAT);
            case Type.LONG -> new APrimitive(APrimitive.APrimitiveType.LONG);
            case Type.DOUBLE -> new APrimitive(APrimitive.APrimitiveType.DOUBLE);
            case Type.OBJECT -> new JVMClass(type.getClassName());
            case Type.ARRAY -> new AArray(getType(type.getElementType()));
            default -> {
                throw new NullPointerException("cant fetch type");
            }
        };
    }

    public static AField getField(AClass owner, FieldNode node) {
        return new AField(node.access, node.name, getType(Type.getType(node.desc)), owner);
    }

    public static AMethod getMethod(AClass owner, MethodNode node) {
        var argumentTypes = Type.getArgumentTypes(node.desc);
        var returnType = Converter.getType(Type.getReturnType(node.desc));
        var args = Arrays.stream(argumentTypes).map(Converter::getType).toList();
        return new AMethod(node.access, node.name, returnType, args, owner);
    }
}
