package de.plixo.tir.expr;

import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.StructImplementation;
import de.plixo.typesys.types.Type;
import lombok.Getter;

import java.util.List;

public final class ConstructExpr implements Expr {

    @Getter
    private final Unit.Structure structure;

    @Getter
    private final List<Expr> arguments;

    @Getter
    private final StructImplementation implementation;

    public ConstructExpr(Unit.Structure structure, List<Expr> arguments,
                         StructImplementation implementation) {
        this.structure = structure;
        this.arguments = arguments;
        this.implementation = implementation;
    }

    @Override
    public Type getType() {
        return implementation;
    }

    @Override
    public void fillType() {

    }
}
