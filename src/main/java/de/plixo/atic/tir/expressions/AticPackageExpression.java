package de.plixo.atic.tir.expressions;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.path.Package;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class AticPackageExpression extends Expression {
    private final Package thePackage;

    @Override
    public Type getType(Context context) {
        return new VoidType();
    }
}
