package de.plixo.atic.tir.aticclass;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parameter {
    @Getter
    private final String name;
    @Getter
    private final AType type;

}
