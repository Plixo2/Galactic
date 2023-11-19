package de.plixo.atic.types.sub;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AField {
    public final int modifier;
    public final String name;
    public final AType type;
    public final AClass owner;

    @Override
    public String toString() {
        return "AField{" + "name='" + name + '\'' + ", type=" + type + '}';
    }

    public String getDescriptor() {
        return type.getDescriptor();
    }
}
