package de.plixo.atic.types.sub;

import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Field {
    private final int modifier;
    private final String name;
    private final Type type;
    private final Class owner;

    @Override
    public String toString() {
        return "AField{" + "name='" + name + '\'' + ", type=" + type + '}';
    }

    public String getDescriptor() {
        return type.getDescriptor();
    }
}
