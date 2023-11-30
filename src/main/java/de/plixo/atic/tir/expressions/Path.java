package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Path extends Expression {
    private final ObjectPath path;


    @Override
    public AType getType() {
        return new AVoid();
    }
}
