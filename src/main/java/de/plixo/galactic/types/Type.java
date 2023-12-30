package de.plixo.galactic.types;


import de.plixo.galactic.typed.Context;

/**
 * Default Type class for the Language
 */
public abstract class Type {


    public abstract char getJVMKind();

    public abstract String getDescriptor();

    public abstract boolean equals(Object o);

    /**
     * test if a is lower in the chain then b
     * so that let v: a = b is allowed
     *
     * @param a potential superclass of b
     * @param b potential implementation of a
     * @return if a can be assigned with a value from type b
     */
    public static boolean isAssignableFrom(Type a, Type b, Context context) {
        var same = isSame(a, b);
        if (same) {
            return true;
        }
        if ((b instanceof Class bClass) && (a instanceof Class)) {
            var superType = bClass.getSuperClass();
            if (superType == null) {
                return false;
            }
            for (var anInterface : bClass.getInterfaces()) {
                if (isAssignableFrom(a, anInterface, context)) {
                    return true;
                }
            }
            return isAssignableFrom(a, superType, context);
        }
        if ((b instanceof ArrayType bClass && bClass.elementType() instanceof Class clazz) &&
                (a instanceof ArrayType arrayType)) {
            var superType = clazz.getSuperClass();
            if (superType == null) {
                return false;
            }
            for (var anInterface : clazz.getInterfaces()) {
                if (isAssignableFrom(arrayType.elementType(), anInterface, context)) {
                    return true;
                }
            }
            return isAssignableFrom(arrayType.elementType(), superType, context);
        }

        return false;
    }


    public static boolean isSame(Type a, Type b) {
        return a.equals(b);
    }
}
