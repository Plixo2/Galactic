package de.plixo.atic.types.sub;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AField {
    private final int modifier;
    private final String name;
    private final AType type;
    private final AClass owner;

    @Override
    public String toString() {
        return "AField{" + "name='" + name + '\'' + ", type=" + type + '}';
    }

    public String getDescriptor() {
        return type.getDescriptor();
    }
}
