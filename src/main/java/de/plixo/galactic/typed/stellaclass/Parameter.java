package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static de.plixo.galactic.typed.Scope.INPUT;

@RequiredArgsConstructor
public class Parameter {
    @Getter
    private final String name;
    @Getter
    private final Type type;
    @Getter
    private final Scope.Variable variable;

    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
        this.variable = new Scope.Variable(name, INPUT, type, null);
    }
}
