package de.plixo.atic.tir;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    public Variable addVariable(String name, int type) {
        var variable = new Variable(name, type, null);
        variables.add(variable);
        return variable;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final int variableType;

        private final @Nullable Variable outsideClosure;
    }

    public static int INPUT = 1;
    public static int FINAL = 1 << 1;
    public static int CLOSURE_CAPTURE = 1 << 2;

}
