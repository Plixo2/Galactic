package de.plixo.galactic.typed;

import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Scope for variables
 */
public class Scope {

    @Getter
    @Setter
    private @Nullable Scope parent;

    public Scope(@Nullable Scope parent) {
        this.parent = parent;
    }

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

    public List<Variable> getAllVariables() {
        var variables = new ArrayList<>(this.variables);
        if (parent != null) {
            variables.addAll(parent.getAllVariables());
        }
        return variables;
    }


    public void addVariable(Variable variable) {
        variables.add(variable);
    }


    @Getter
    @ToString
    public static class Variable {
        private final String name;
        private final int flags;

        @Setter
        @Accessors(fluent = false)
        private @Nullable Type type;

        public Variable(String name, int flags, @Nullable Type type) {
            this.name = name;
            this.flags = flags;
            this.type = type;
        }

        private int usageCount = 0;

        public void addUsage() {
            usageCount += 1;
        }

        public boolean isFinal() {
            return (flags & FINAL) != 0;
        }
    }

    @Getter
    @ToString
    public static class ClosureVariable extends Variable {
        private final Variable outsideClosure;

        @Setter
        Variable fieldOwner;
        @Setter
        StellaClass owner;
        @Setter
        private Field field;

        public ClosureVariable(Variable outsideClosure) {
            super(outsideClosure.name, FINAL, outsideClosure.type);
            assert this.isFinal();
            this.outsideClosure = outsideClosure;
        }

        @Override
        public Type getType() {
            return outsideClosure.type;
        }
    }

    /**
     * Flags for Variables types
     */

    public static int INPUT = 1;
    public static int FINAL = 1 << 1;
    public static int CLOSURE_CAPTURE = 1 << 2;
    public static int THIS = 1 << 3;

}
