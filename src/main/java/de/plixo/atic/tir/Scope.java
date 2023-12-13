package de.plixo.atic.tir;

import de.plixo.atic.types.Type;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Scope {

    @Getter
    private @Nullable
    final Scope parent;
    private final List<Variable> variables = new ArrayList<>();

    public @Nullable Variable getVariable(String name) {
        for (var variable : variables) {
            if (variable.name.equals(name)) {
                return variable;
            }
        }
        if (parent != null) {
            return parent.getVariable(name);
        }
        return null;
    }



    public void addVariable(Variable variable) {
        variables.add(variable);
    }

    @AllArgsConstructor
    @ToString
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final int flags;

        @Getter
        @Setter
        @Accessors(fluent = false)
        private @Nullable Type type;

        private final @Nullable Variable outsideClosure;
    }

    public static int INPUT = 1;
    public static int FINAL = 1 << 1;
    public static int CLOSURE_CAPTURE = 1 << 2;

}
