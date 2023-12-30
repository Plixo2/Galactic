package de.plixo.galactic.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Represents an array type.
 */
@RequiredArgsConstructor
@Getter
public class ArrayType extends Type {
    private final Type elementType;

    @Override
    public String toString() {
        return "Array[" + elementType + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(elementType, arrayType.elementType);
    }

    @Override
    public char getJVMKind() {
        return '[';
    }


    @Override
    public String getDescriptor() {
        return getJVMKind() + elementType.getDescriptor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType);
    }
}
