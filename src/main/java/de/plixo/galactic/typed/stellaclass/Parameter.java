package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import static de.plixo.galactic.typed.Scope.INPUT;

/**
 * Standard Parameter for a method, constructor or lambda
 */
@Getter
@RequiredArgsConstructor
public class Parameter {
    private final String name;
    private final Type type;
    private final Scope.Variable variable;

    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
        this.variable = new Scope.Variable(name, INPUT, type);
    }
}
