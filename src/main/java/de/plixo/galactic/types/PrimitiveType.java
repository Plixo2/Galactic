package de.plixo.galactic.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor
public class PrimitiveType extends Type {
    public StellaPrimitiveType typeOfPrimitive;

    public static final PrimitiveType INT = new PrimitiveType(StellaPrimitiveType.INT);
    public static final PrimitiveType BYTE = new PrimitiveType(StellaPrimitiveType.BYTE);
    public static final PrimitiveType SHORT = new PrimitiveType(StellaPrimitiveType.SHORT);
    public static final PrimitiveType LONG = new PrimitiveType(StellaPrimitiveType.LONG);
    public static final PrimitiveType FLOAT = new PrimitiveType(StellaPrimitiveType.FLOAT);
    public static final PrimitiveType DOUBLE = new PrimitiveType(StellaPrimitiveType.DOUBLE);
    public static final PrimitiveType BOOLEAN = new PrimitiveType(StellaPrimitiveType.BOOLEAN);
    public static final PrimitiveType CHAR = new PrimitiveType(StellaPrimitiveType.CHAR);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveType that = (PrimitiveType) o;
        return typeOfPrimitive == that.typeOfPrimitive;
    }

    public boolean equals(StellaPrimitiveType primitiveType) {
        return this.typeOfPrimitive == primitiveType;
    }

    public boolean isNumber() {
        return typeOfPrimitive.isNumeric();
    }


    @Override
    public String toString() {
        return typeOfPrimitive.name();
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeOfPrimitive);
    }

    @Override
    public int JVMSize() {
        return switch (typeOfPrimitive) {
            case INT, FLOAT, BOOLEAN, CHAR, BYTE, SHORT -> 1;
            case LONG, DOUBLE -> 2;
        };
    }

    @Override
    public char getJVMKind() {
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
        return String.valueOf(getJVMKind());
    }

    @Getter
    @RequiredArgsConstructor
    public enum StellaPrimitiveType {
        INT(true),
        BYTE(true),
        SHORT(true),
        LONG(true),
        FLOAT(true),
        DOUBLE(true),
        BOOLEAN(false),
        CHAR(true);

        private final boolean isNumeric;

        public static @Nullable PrimitiveType.StellaPrimitiveType fromHIR(
                de.plixo.galactic.high_level.PrimitiveType primitiveType) {
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
