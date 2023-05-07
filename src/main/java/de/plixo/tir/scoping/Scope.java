package de.plixo.tir.scoping;

import de.plixo.typesys.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scope {

    @Nullable
    private final Scope parent;

    private final Map<String, Variable> definitions = new LinkedHashMap<>();
    private final Map<String, Variable> input = new LinkedHashMap<>();

    @Getter
    private final Type returnType;

    public Scope(@Nullable Scope parent, Type returnType) {
        this.parent = parent;
        this.returnType = returnType;
    }

    public void addVariable(Variable variable) {
        definitions.put(variable.name, variable);
    }

    public @Nullable Variable get(String name) {
        var variable = definitions.get(name);
        if (variable != null) {
            return variable;
        }
        if (parent != null) {
            return parent.get(name);
        }
        return null;
    }

    @AllArgsConstructor
    public static class Variable {
        @Getter
        private String name;

        @Getter
        private Type type;
    }
}
