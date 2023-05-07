package de.plixo.tir.expr;

import de.plixo.common.Constant;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public final class ConstantExpr implements Expr {

    @Getter
    private final Constant constant;

    public ConstantExpr(Constant constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return switch (constant) {
            case Constant.BoolConstant ignored -> Primitive.BOOL;
            case Constant.NumberConstant numberConstant -> {
                if (numberConstant.number instanceof Double) {
                    yield Primitive.FLOAT;
                } else {
                    yield Primitive.INT;
                }
            }
            case Constant.StringConstant ignored -> Primitive.STRING;
        };
    }

    @Override
    public void fillType() {

    }
}
