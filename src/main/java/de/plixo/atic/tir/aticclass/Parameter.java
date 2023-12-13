package de.plixo.atic.tir.aticclass;

import de.plixo.atic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parameter {
    @Getter
    private final String name;
    @Getter
    private final Type type;

}
