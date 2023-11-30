package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.path.Package;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class AticClassExpression extends Expression{
    private final AticClass theClass;

    @Override
    public AType getType() {
        //TODO return the class type here, like in Java some.class
        throw new NullPointerException("AticClassExpression has no type");
    }
}
