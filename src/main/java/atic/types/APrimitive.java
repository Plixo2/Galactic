package atic.types;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class APrimitive extends AType {
    public APrimitiveType typeOfPrimitive;

    @Override
    public String toString(Set<AType> types) {
        if (types.contains(this)) {
            return simple();
        }
        types.add(this);
        return "APrimitive{" + "typeOfPrimitive=" + typeOfPrimitive + '}';
    }

    @Override
    public String simple() {
        return "APrimitive";
    }

    public enum APrimitiveType {
        INT,
        BYTE,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        CHAR,
    }
}
