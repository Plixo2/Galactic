package de.plixo.atic.types;

import de.plixo.atic.common.PrimitiveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor
public class APrimitive extends AType {
    public APrimitiveType typeOfPrimitive;

    public static final APrimitive INT = new APrimitive(APrimitiveType.INT);
    public static final APrimitive BYTE = new APrimitive(APrimitiveType.BYTE);
    public static final APrimitive SHORT = new APrimitive(APrimitiveType.SHORT);
    public static final APrimitive LONG = new APrimitive(APrimitiveType.LONG);
    public static final APrimitive FLOAT = new APrimitive(APrimitiveType.FLOAT);
    public static final APrimitive DOUBLE = new APrimitive(APrimitiveType.DOUBLE);
    public static final APrimitive BOOLEAN = new APrimitive(APrimitiveType.BOOLEAN);
    public static final APrimitive CHAR = new APrimitive(APrimitiveType.CHAR);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APrimitive that = (APrimitive) o;
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

        public static @Nullable APrimitiveType fromHIR(PrimitiveType primitiveType) {
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
