package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.expressions.Expression;

public class CheckFlow implements Tree {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for CheckFlow stage");
    }
}