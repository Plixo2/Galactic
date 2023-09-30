package atic.types;

import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class Converter {

    Map<Class<?>, AType> map = new HashMap<>();

    public AType getType(Type type) {
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
            case Type.OBJECT -> new AClass(type.getClassName(), AClass.ClassOrigin.JVM);
            case Type.ARRAY -> new AArray(getType(type.getElementType()));
            case default -> {
                throw new NullPointerException("cant fetch type");
            }
        };
    }

//    public AType getType(Class<?> theClass) {
//        if (map.containsKey(theClass)) {
//            return map.get(theClass);
//        }
//        var primitive = toPrimitive(theClass);
//        if (primitive != null) {
//            var value = new APrimitive(primitive);
//            map.put(theClass, value);
//            return value;
//        }
//        if (theClass.equals(void.class)) {
//            var value = new AVoid();
//            map.put(theClass, value);
//            return value;
//        }
//        if (theClass.isArray()) {
//            var aArray = new AArray();
//            map.put(theClass, aArray);
//            aArray.elementType = getType(theClass.getComponentType());
//            return aArray;
//        }
//
//        var aClass = new AClass();
//        aClass.name = theClass.getName();
//        map.put(theClass, aClass);
//        for (var field : theClass.getFields()) {
//            if (Modifier.isStatic(field.getModifiers()) ||
//                    !Modifier.isPublic(field.getModifiers())) {
//                continue;
//            }
//            var type = getType(field.getType());
//            var name = field.getName();
//            var value = new AField(field.getModifiers(), name, type);
//            aClass.fields.put(name, value);
//        }
//
//        return aClass;
//
//    }
//
//    private static @Nullable APrimitive.APrimitiveType toPrimitive(Class<?> clazz) {
//        if (clazz.equals(int.class)) {
//            return APrimitive.APrimitiveType.INT;
//        } else if (clazz.equals(byte.class)) {
//            return APrimitive.APrimitiveType.BYTE;
//        } else if (clazz.equals(short.class)) {
//            return APrimitive.APrimitiveType.SHORT;
//        } else if (clazz.equals(long.class)) {
//            return APrimitive.APrimitiveType.LONG;
//        } else if (clazz.equals(float.class)) {
//            return APrimitive.APrimitiveType.FLOAT;
//        } else if (clazz.equals(double.class)) {
//            return APrimitive.APrimitiveType.DOUBLE;
//        } else if (clazz.equals(boolean.class)) {
//            return APrimitive.APrimitiveType.BOOLEAN;
//        } else if (clazz.equals(char.class)) {
//            return APrimitive.APrimitiveType.CHAR;
//        }
//        return null;
//    }
}
