package atic.types;

import java.util.Set;

public class AVoid extends AType {


    @Override
    public String toString(Set<AType> types) {
        if (types.contains(this)) {
            return simple();
        }
        types.add(this);
        return "AVoid";
    }

    @Override
    public String simple() {
        return "AVoid";
    }
}
