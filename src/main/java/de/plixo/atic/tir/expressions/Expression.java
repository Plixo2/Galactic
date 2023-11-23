package de.plixo.atic.tir.expressions;

import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;

import java.util.List;

public sealed abstract class Expression
        permits ArrayConstructExpression, BlockExpression, BooleanExpression, BranchExpression,
        CallNotation, ClassExpression, ConstructExpression, DotNotation, MethodInvokeExpression,
        NumberExpression, ObjectFieldExpression, ObjectMethodExpression, Path,
        StaticFieldExpression, StaticMethodExpression, StringExpression, SymbolExpression,
        UnaryExpression, VarDefExpression, VarExpression {

    public Expression dotNotation(String id, Context context) {
        throw new NullPointerException("dot not implemented for type " + this.getClass().getName());
    }

    public Expression callExpression(List<HIRExpression> arguments, Context context) {
        throw new NullPointerException(
                "call not implemented for type " + this.getClass().getName());
    }


    public Expression standartDotExpression(AType type, String id, Context context) {
        var aField = type.getField(id, context);
        if (aField != null) {
            return new ObjectFieldExpression(this, aField);
        } else {
            var methods = type.getMethods(id, context);
            if (methods.isEmpty()) {
                throw new NullPointerException("cant find field or method " + id);
            }
            return new ObjectMethodExpression(this, methods);
        }
    }

    public abstract AType getType();
}
