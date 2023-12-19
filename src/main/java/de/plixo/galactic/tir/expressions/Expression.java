package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.hir.expressions.HIRExpression;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.types.Type;

import java.util.List;

/**
 * Base class for all expressions,
 * Not all Expressions are used in all stages
 * see flow.md
 */
public sealed abstract class Expression
        permits ArrayConstructExpression, AssignExpression, StellaClassConstructExpression,
        StaticClassExpression, StellaPackageExpression, BlockExpression, BooleanExpression,
        BranchExpression, CallNotation, ClassExpression, ConstructExpression, DotNotation,
        FieldExpression, GetMethodExpression, InstanceCreationExpression, LocalVariableAssign,
        MethodCallExpression, NumberExpression, Path, StaticFieldExpression, StaticMethodExpression,
        StringExpression, SymbolExpression, UnaryExpression, UnitExpression, VarDefExpression,
        VarExpression {

    public Expression dotNotation(String id, Context context) {
        throw new NullPointerException("dot not implemented for type " + this.getClass().getName());
    }

    public Expression callExpression(List<HIRExpression> arguments, Context context) {
        throw new NullPointerException(
                "call not implemented for type " + this.getClass().getName());
    }


    public Expression standartDotExpression(Type type, String id, Context context) {
        var aField = type.getField(id, context);
        if (aField != null) {
            return new FieldExpression(this, type, aField);
        } else {
            var methods = type.getMethods(id, context);
            if (methods.isEmpty()) {
                throw new NullPointerException("cant find field or method " + id);
            }
            return new GetMethodExpression(this, methods);
        }
    }

    public abstract Type getType(Context context);
}
