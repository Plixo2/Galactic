package de.plixo.galactic.tir.stages;

import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.tir.expressions.Expression;

public class CheckFlow implements Tree {
    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for CheckFlow stage");
    }
}