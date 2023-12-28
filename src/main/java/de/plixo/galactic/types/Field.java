package de.plixo.galactic.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Modifier;

@AllArgsConstructor
@Getter
public class Field {
    private final int modifier;
    private final String name;
    private final Type type;
    private final Class owner;

    @Override
    public String toString() {
        return "Field " + name + "(" + getDescriptor() + ")";
    }

    public String getDescriptor() {
        return type.getDescriptor();
    }


    public boolean isStatic() {
        return Modifier.isStatic(modifier);
    }
    public boolean isPublic() {
        return Modifier.isPublic(modifier);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifier);
    }
}
