package de.plixo.atic.v2.tir.type;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.Expression;
import de.plixo.atic.v2.tir.expressions.JVMClass;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ClassType extends Type {

    private final @Nullable Type superType;
    private final List<String> path;

    @Override
    public Class<?> toJVMClass() {
        if (superType == null) {
            return Object.class;
        }
        if (superType instanceof JVMClassType classType) {
            return classType.theClass();
        } else {
            return superType.toJVMClass();
        }
    }

    @Override
    public Expression dotNotation(Expression expression, String id, Context context) {

        return null;
    }

    @Override
    public Expression callNotation(Expression expression, List<HIRExpression> arguments,
                                   Context context) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassType classType = (ClassType) o;
        return Objects.equals(path, classType.path);
    }
}
