package de.plixo.atic.tir.expressions;

import de.plixo.atic.hir.UnaryFunctions;
import de.plixo.atic.tir.Context;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnaryExpression extends Expression {

    @Getter
    private final Expression object;
    @Getter
    private final UnaryFunctions function;

    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(getType(), id, context);
    }

    @Override
    public AType getType() {
        return validate();
    }

    public AType validate() {
        var type = object.getType();
        if ((!(type instanceof APrimitive primitive))) {
            throw new NullPointerException("unary only for primitives " + type);
        }
        switch (function) {
            case NEGATE_LOGIC -> {
                if (!primitive.equals(APrimitive.APrimitiveType.BOOLEAN)) {
                    throw new NullPointerException("only negate (logic) a boolean");
                }
            }
            case MINUS -> {
                if (!primitive.isNumber()) {
                    throw new NullPointerException("only negate (numeric) a number");
                }
            }
            case ADD -> {
                if (!primitive.isNumber()) {
                    throw new NullPointerException("invalid for boolean");
                }
            }
        }
        return primitive;
    }
}