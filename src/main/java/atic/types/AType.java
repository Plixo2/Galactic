package atic.types;

import java.util.HashSet;
import java.util.Set;

public abstract class AType {

    public abstract String toString(Set<AType> types);
    public abstract String simple();

//    @Override
//    public String toString() {
//        return simple();
//    }


    @Override
    public String toString() {
        return simple();
    }
}
