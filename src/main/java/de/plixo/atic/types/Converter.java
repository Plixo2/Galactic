package de.plixo.atic.types;

public class Converter {



//    @SneakyThrows
//    public static Type getType(org.objectweb.asm.Type type) {
//        return switch (type.getSort()) {
//            case org.objectweb.asm.Type.VOID -> new VoidType();
//            case org.objectweb.asm.Type.BOOLEAN -> new PrimitiveType(PrimitiveType.APrimitiveType.BOOLEAN);
//            case org.objectweb.asm.Type.CHAR -> new PrimitiveType(PrimitiveType.APrimitiveType.CHAR);
//            case org.objectweb.asm.Type.BYTE -> new PrimitiveType(PrimitiveType.APrimitiveType.BYTE);
//            case org.objectweb.asm.Type.SHORT -> new PrimitiveType(PrimitiveType.APrimitiveType.SHORT);
//            case org.objectweb.asm.Type.INT -> new PrimitiveType(PrimitiveType.APrimitiveType.INT);
//            case org.objectweb.asm.Type.FLOAT -> new PrimitiveType(PrimitiveType.APrimitiveType.FLOAT);
//            case org.objectweb.asm.Type.LONG -> new PrimitiveType(PrimitiveType.APrimitiveType.LONG);
//            case org.objectweb.asm.Type.DOUBLE -> new PrimitiveType(PrimitiveType.APrimitiveType.DOUBLE);
//            case org.objectweb.asm.Type.ARRAY -> new ArrayType(getType(type.getElementType()));
//            case org.objectweb.asm.Type.OBJECT -> {
//                System.out.println("type.getClassName() = " + type.getClassName());
//                yield new JVMClass(type.getClassName());
//            }
//            default -> {
//                throw new NullPointerException("cant fetch type");
//            }
//        };
//    }
//
//    public static Field getField(Class owner, FieldNode node) {
//        return new Field(node.access, node.name, getType(org.objectweb.asm.Type.getType(node.desc)), owner);
//    }
//
//    public static Method getMethod(Class owner, MethodNode node) {
//        var argumentTypes = org.objectweb.asm.Type.getArgumentTypes(node.desc);
//        var returnType = Converter.getType(org.objectweb.asm.Type.getReturnType(node.desc));
//        var args = Arrays.stream(argumentTypes).map(Converter::getType).toList();
//        return new Method(node.access, node.name, returnType, args, owner);
//    }
}
