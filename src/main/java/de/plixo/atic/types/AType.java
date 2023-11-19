package de.plixo.atic.types;


import de.plixo.atic.types.sub.AField;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import org.jetbrains.annotations.Nullable;

public abstract class AType {


    public @Nullable AField getField(String id, Context context) {
        return null;
    }

    public MethodCollection getMethods(String id, Context context) {
        return null;
    }

    public abstract char getKind();

    public String getDescriptor() {
        return String.valueOf(getKind());
    }

    /**
     * test if a is lower in the chain then b
     * so that let v: a = b is allowed
     *
     * @param a potential superclass of b
     * @param b potential implementation of a
     * @return if a can be assigned with a value from type b
     */
    public static boolean isAssignableFrom(AType a, AType b, Context context) {
        var same = isSame(a, b);
        if (same) {
            return true;
        }
        if ((b instanceof AClass bClass) && (a instanceof AClass aClass)) {
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
        return false;
    }


    public static boolean isSame(AType a, AType b) {
        return a.equals(b);
    }
}
