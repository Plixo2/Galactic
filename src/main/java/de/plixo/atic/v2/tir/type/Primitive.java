package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Primitive extends Type {

    @Getter
    private final PrimitiveType type;

    public static Primitive VOID = new Primitive(PrimitiveType.VOID);
    public static Primitive INT = new Primitive(PrimitiveType.INT);
    public static Primitive BYTE = new Primitive(PrimitiveType.BYTE);
    public static Primitive SHORT = new Primitive(PrimitiveType.SHORT);
    public static Primitive LONG = new Primitive(PrimitiveType.LONG);
    public static Primitive FLOAT = new Primitive(PrimitiveType.FLOAT);
    public static Primitive DOUBLE = new Primitive(PrimitiveType.DOUBLE);
    public static Primitive BOOLEAN = new Primitive(PrimitiveType.BOOLEAN);
    public static Primitive CHAR = new Primitive(PrimitiveType.CHAR);


    public static @Nullable Primitive toPrimitive(Class<?> clazz) {
        if(clazz.equals(void.class)) {
            return Primitive.VOID;
        }
        else if(clazz.equals(int.class)) {
            return Primitive.INT;
        }
        else if(clazz.equals(byte.class)) {
            return Primitive.BYTE;
        }
        else if(clazz.equals(short.class)) {
            return Primitive.SHORT;
        }
        else if(clazz.equals(long.class)) {
            return Primitive.LONG;
        }
        else if(clazz.equals(float.class)) {
            return Primitive.FLOAT;
        }
        else if(clazz.equals(double.class)) {
            return Primitive.DOUBLE;
        }
        else if(clazz.equals(boolean.class)) {
            return Primitive.BOOLEAN;
        }
        else if(clazz.equals(char.class)) {
            return Primitive.CHAR;
        }
        return null;
    }

    @Override
    public Class<?> toJVMClass() {
        return switch (type) {
            case VOID -> void.class;
            case INT -> int.class;
            case BYTE -> byte.class;
            case SHORT -> short.class;
            case LONG -> long.class;
            case FLOAT -> float.class;
            case DOUBLE -> double.class;
            case BOOLEAN -> boolean.class;
            case CHAR -> char.class;
        };
    }

    public Class<?> toBoxed() {
        return switch (type) {
            case VOID -> Void.class;
            case INT -> Integer.class;
            case BYTE -> Byte.class;
            case SHORT -> Short.class;
            case LONG -> Long.class;
            case FLOAT -> Float.class;
            case DOUBLE -> Double.class;
            case BOOLEAN -> Boolean.class;
            case CHAR -> Character.class;
        };
    }

    @Override
    public Expression dotNotation(Expression expression, String id, Context context) {
        throw new NullPointerException("not impl");
    }

    @Override
    public Expression callNotation(Expression expression, List<HIRExpression> arguments, Context context) {
        throw new NullPointerException("not impl");
    }

    public enum PrimitiveType {
        VOID,
        INT,
        BYTE,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR,
    }

    @Override
    public String toString() {
        return "Primitive{" + "type=" + type + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Primitive primitive = (Primitive) o;
        return type == primitive.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public @Nullable Type getSuperType() {
        return JVMClassType.fromClass(toBoxed());
    }
}
