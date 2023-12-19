package de.plixo.galactic.types;

import java.util.Objects;

public class VoidType extends Type {


    @Override
    public String toString() {
        return "AVoid";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass() == VoidType.class;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode("V");
    }

    @Override
    public char getKind() {
        return 'V';
    }

    @Override
    public String getDescriptor() {
        return String.valueOf(getKind());
    }
}
