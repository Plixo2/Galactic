package atic.compile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) {
        try {
            ClassNode cn = new ClassNode();
            ClassReader cr = new ClassReader(getInputStream(TestObj.class));
            cr.accept(cn, 0);
            for (FieldNode field : cn.fields) {
                System.out.println();
                System.out.println("field.name = " + field.name);


                var type = Type.getType(field.desc);
                System.out.println("field.signature = " + field.signature);
                System.out.println("type.getClassName() = " + type.getClassName());
                if (type.getSort() == Type.ARRAY) {
                    System.out.println("type.getElementType().getClassName() = " +
                            type.getElementType().getClassName());
                }
                System.out.println(
                        "sortToString(type.getSort()) = " + sortToString(type.getSort()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(Class<?> clazz) {
        String classFile = "/" + clazz.getName().replace('.', '/') + ".class";
        return clazz.getResourceAsStream(classFile);
    }

    private static String sortToString(int sort) {
        return switch (sort) {
            case Type.VOID -> "VOID";
            case Type.BOOLEAN -> "BOOLEAN";
            case Type.CHAR -> "CHAR";
            case Type.BYTE -> "BYTE";
            case Type.SHORT -> "SHORT";
            case Type.INT -> "INT";
            case Type.FLOAT -> "FLOAT";
            case Type.LONG -> "LONG";
            case Type.DOUBLE -> "DOUBLE";
            case Type.OBJECT -> "OBJECT";
            case Type.ARRAY -> "ARRAY";
            default -> "UNKNOWN";
        };
    }
}
