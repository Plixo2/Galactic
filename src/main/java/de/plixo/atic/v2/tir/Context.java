package de.plixo.atic.v2.tir;

import de.plixo.atic.v2.tir.type.Type;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Context {

    private @Nullable Context parent;
    private final List<Variable> variables = new ArrayList<>();
    private final Unit unit;


    public Variable addVariable(String name, Type type, VariableType variableType) {
        var variable = new Variable(name, type, variableType);
        variables.add(variable);
        return variable;
    }

    public @Nullable Variable getVariable(String id) {
        for (var variable : variables) {
            if (variable.name.equals(id)) {
                return variable;
            }
        }
        if (parent == null) {
            return null;
        }
        return parent.getVariable(id);

    }

    public Context childContext() {
        return new Context(this, unit);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final Type type;
        @Getter
        private final VariableType variableType;
    }

    public enum VariableType {
        INPUT,
        LOCAL
    }


}
