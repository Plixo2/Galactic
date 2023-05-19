package de.plixo.atic.tir.scoping;

import de.plixo.atic.typing.types.StructImplementation;
import de.plixo.atic.typing.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.sql.Struct;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {

    @Nullable
    private final Scope parent;

    private final Map<String, Variable> definitions = new LinkedHashMap<>();
    private final Map<String, Variable> input = new LinkedHashMap<>();

    @Getter
    private final Type returnType;

    @Getter
    private final Type owner;

    @Getter
    private final int scopeLevel;

    public Scope(@Nullable Scope parent, Type returnType, Type owner, int scopeLevel) {
        this.parent = parent;
        this.returnType = returnType;
        this.scopeLevel = scopeLevel;
        this.owner = owner;
    }

    public void addVariable(Variable variable) {
        if (get(variable.name) != null) {
            throw new NullPointerException("variable " + variable.name + " does already exist");
        }
        definitions.put(variable.name, variable);
    }

    public void overwriteVariable(Variable variable) {
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


    public static class Variable {
        @Getter
        private String name;

        @Getter
        private Type type;

        @Getter
        private AllocationType allocationType;

        @Getter
        private DefineType defineType;

        @Getter
        @Setter
        @Accessors(fluent = true)
        private boolean usedInClosure = false;

        public Variable(String name, Type type, AllocationType allocationType,
                        DefineType defineType) {
            this.name = name;
            this.type = type;
            this.allocationType = allocationType;
            this.defineType = defineType;
        }
    }

    public enum DefineType {
        DYNAMIC,
        FINAL
    }

    public enum AllocationType {
        STACK,
        SELF,
        INPUT
    }
}
