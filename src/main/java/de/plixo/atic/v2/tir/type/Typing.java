package de.plixo.atic.v2.tir.type;

import java.util.Arrays;

public class Typing {

    public static Type toAticType(Class<?> clazz) {
        throw new NullPointerException("not impl");
    }

    /**
     * test if a is lower in the chain then b
     * so that let v: a = b is allowed
     *
     * @param a potential superclass of b
     * @param b potential implementation of a
     * @return if a can be assigned with a value from type b
     */
    public static boolean isAssignableFrom(Type a, Type b) {
        var same = isSame(a, b);
        if (same) {
            return true;
        }
        var superType = b.getSuperType();
        if (superType == null) {
            return false;
        }
        return isAssignableFrom(a, superType);
    }


    private static boolean isSame(Type a, Type b) {
        return a.equals(b);
    }
}
