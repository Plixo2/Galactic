package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.hir.UnaryFunctions;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.PrimitiveType;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class UnaryExpression extends Expression {
    private final Region region;
    private final Expression object;
    private final UnaryFunctions function;


    @Override
    public Type getType(Context context) {
        return validate(context);
    }

    public Type validate(Context context) {
        var type = object.getType(context);
        if ((!(type instanceof PrimitiveType primitive))) {
            throw new NullPointerException("unary only for primitives " + type);
        }
        switch (function) {
            case NEGATE_LOGIC -> {
                if (!primitive.equals(PrimitiveType.StellaPrimitiveType.BOOLEAN)) {
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
