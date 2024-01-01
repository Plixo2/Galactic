package de.plixo.galactic.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents a field (a variable) in a class.
 */
@AllArgsConstructor
@Getter
public class Field {
    private final int modifier;
    private final String name;
    @Setter
    private @Nullable Type type;
    @Setter
    private @Nullable Class owner;

    @Override
    public String toString() {
        return STR."Field \{name}(\{getDescriptor()})";
    }

    public String getDescriptor() {
        return Objects.requireNonNull(type).getDescriptor();
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
