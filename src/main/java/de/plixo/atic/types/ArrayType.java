package de.plixo.atic.types;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class ArrayType extends Type {
    public Type elementType;
    @Override
    public String toString() {
        return "AArray{" + "elementType=" + elementType + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(elementType, arrayType.elementType);
    }

    @Override
    public char getKind() {
        return '[';
    }

    @Override
    public String getDescriptor() {
        return getKind() + elementType.getDescriptor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType);
    }
}
