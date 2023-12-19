package de.plixo.galactic.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor
public class PrimitiveType extends Type {
    public APrimitiveType typeOfPrimitive;

    public static final PrimitiveType INT = new PrimitiveType(APrimitiveType.INT);
    public static final PrimitiveType BYTE = new PrimitiveType(APrimitiveType.BYTE);
    public static final PrimitiveType SHORT = new PrimitiveType(APrimitiveType.SHORT);
    public static final PrimitiveType LONG = new PrimitiveType(APrimitiveType.LONG);
    public static final PrimitiveType FLOAT = new PrimitiveType(APrimitiveType.FLOAT);
    public static final PrimitiveType DOUBLE = new PrimitiveType(APrimitiveType.DOUBLE);
    public static final PrimitiveType BOOLEAN = new PrimitiveType(APrimitiveType.BOOLEAN);
    public static final PrimitiveType CHAR = new PrimitiveType(APrimitiveType.CHAR);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveType that = (PrimitiveType) o;
        return typeOfPrimitive == that.typeOfPrimitive;
    }

    public boolean equals(APrimitiveType primitiveType) {
        return this.typeOfPrimitive == primitiveType;
    }

    public boolean isNumber() {
        return typeOfPrimitive.isNumeric();
    }


    @Override
    public String toString() {
        return "APrimitive{" + typeOfPrimitive + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeOfPrimitive);
    }

    @Override
    public char getKind() {
        return switch (typeOfPrimitive) {
            case INT -> 'I';
            case BYTE -> 'B';
            case SHORT -> 'S';
            case LONG -> 'J';
            case FLOAT -> 'F';
            case DOUBLE -> 'D';
            case BOOLEAN -> 'Z';
            case CHAR -> 'C';
        };
    }

    @Override
    public String getDescriptor() {
        return String.valueOf(getKind());
    }

    @RequiredArgsConstructor
    public enum APrimitiveType {
        INT(true),
        BYTE(true),
        SHORT(true),
        LONG(true),
        FLOAT(true),
        DOUBLE(true),
        BOOLEAN(false),
        CHAR(true);

        @Getter
        private final boolean isNumeric;

        public static @Nullable APrimitiveType fromHIR(
                de.plixo.galactic.common.PrimitiveType primitiveType) {
            return switch (primitiveType) {
                case VOID -> null;
                case INT -> INT;
                case BYTE -> BYTE;
                case SHORT -> SHORT;
                case LONG -> LONG;
                case FLOAT -> FLOAT;
                case DOUBLE -> DOUBLE;
                case BOOLEAN -> BOOLEAN;
                case CHAR -> CHAR;
            };
        }


    }
}
