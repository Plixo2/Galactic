package de.plixo.atic.tir.aticclass;

import de.plixo.atic.tir.Scope;
import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class Parameter {
    @Getter
    private final String name;
    @Getter
    private final Type type;
    @Getter
    @Setter
    private @Nullable Scope.Variable variable;
}
