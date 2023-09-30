package atic.types;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class AClass extends AType {
    public String name;
    public ClassOrigin origin;

    @Override
    public String toString(Set<AType> types) {
        if (types.contains(this)) {
            return simple();
        }
        types.add(this);
        return "AClass{" + "name='" + name + '\'' + '}';
    }

    @Override
    public String simple() {
        return "AClass{" + "name='" + name + '\'' + '}';
    }

    public enum ClassOrigin {
        JVM,
        ATIC
    }
}
