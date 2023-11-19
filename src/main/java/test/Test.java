package test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            ClassNode cn = new ClassNode();
            ClassReader cr = new ClassReader(getInputStream(TestInterface.class));
            cr.accept(cn, 0);
            System.out.println(cn.signature);
            var isInterface = (cn.access & Opcodes.ACC_INTERFACE) != 0;
            System.out.println(isInterface);
//            System.out.println("cn.signature = " + cn.signature);
//            var signatureReader = new SignatureReader(cn.signature);
//            var classSignatureNode = new ClassSignatureNode();
//            signatureReader.accept(classSignatureNode);
//            System.out.println("classSignatureNode = " + classSignatureNode);


//            var test = cn.methods.get(1);
//            System.out.println(test.name);
//            System.out.println(test.desc);
//
//            var methodType = Type.getMethodType(test.signature);
//            System.out.println(methodType);
//
//            var signatureReader = new SignatureReader(test.signature);
//            var visitor = new CustomSignatureVisitor(0);
//            signatureReader.accept(visitor);
//
//            System.out.println("Generic Types: " + visitor.generics);

//            System.out.println(Arrays.toString(methodType));
//            for (FieldNode field : cn.fields) {
//                System.out.println();
//                System.out.println("field.name = " + field.name);
//
//
//                var type = Type.getType(field.desc);
//                System.out.println("field.signature = " + field.signature);
//                System.out.println("type.getClassName() = " + type.getClassName());
//                if (type.getSort() == Type.ARRAY) {
//                    System.out.println("type.getElementType().getClassName() = " +
//                            type.getElementType().getClassName());
//                }
//                System.out.println(
//                        "sortToString(type.getSort()) = " + sortToString(type.getSort()));
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class CustomSignatureVisitor extends SignatureVisitor {
        private final List<String> generics = new ArrayList<>();
        int depth;

        public CustomSignatureVisitor(int depth) {
            super(org.objectweb.asm.Opcodes.ASM9);
            this.depth = depth;
        }

        @Override
        public void visitFormalTypeParameter(String name) {
            System.out.println("visitFormalTypeParameter = " + name);
//            generics.add(name);
        }

        @Override
        public void visitClassType(String name) {
            System.out.println("visitClassType = " + name);
        }

        @Override
        public void visitTypeArgument() {
//            generics.add("?");
            System.out.println("visitTypeArgument");
        }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            System.out.println("start = " + wildcard);
//            generics.add(String.valueOf(wildcard));
            var i = (int) (Math.random() * 1000);
            System.out.println("depth " + i);
            return new CustomSignatureVisitor(i);
        }

        @Override
        public void visitBaseType(char descriptor) {
            System.out.println("visitBaseType = " + descriptor);
        }

        @Override
        public void visitTypeVariable(String name) {
            System.out.println("visitTypeVariable = " + name);
        }

        @Override
        public void visitInnerClassType(String name) {
            System.out.println("visitInnerClassType = " + name);
        }

        @Override
        public void visitEnd() {
            System.out.println("end " + depth);
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
