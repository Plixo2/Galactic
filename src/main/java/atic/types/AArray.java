package atic.types;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class AArray extends AType{
    public AType elementType;

    @Override
    public String toString(Set<AType> types) {
        if (types.contains(this)) {
            return simple();
        }
        types.add(this);
        return "AArray{" + "elementType=" + elementType + '}';
    }

    @Override
    public String simple() {
        return "AArray";
    }
}
