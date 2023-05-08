package de.plixo.tir.expr;

import de.plixo.typesys.types.StructImplementation;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class FieldExpr implements Expr {

    @Getter
    private final Expr structure;

    @Getter
    private final String field;

    @Getter
    private final StructImplementation structImplementation;

    @Getter
    private final Type fieldType;


    public FieldExpr(Expr structure, String field, StructImplementation structImplementation,
                     Type fieldType) {
        this.structure = structure;
        this.field = field;
        this.structImplementation = structImplementation;
        this.fieldType = fieldType;
    }

    @Override
    public Type getType() {
        return fieldType;
    }

    @Override
    public void fillType() {

    }
}
