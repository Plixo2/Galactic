package de.plixo.atic.types;

import java.util.Objects;

public class AVoid extends AType {


    @Override
    public String toString() {
        return "AVoid";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass() == AVoid.class;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode("V");
    }

    @Override
    public char getKind() {
        return 'V';
    }
}
