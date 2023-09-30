package de.plixo.atic.v2.tir.expressions;

import de.plixo.atic.v2.hir2.expressions.HIRExpression;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Path extends Expression {

    private final List<String> names;

    @Override
    public Expression callNotation(List<HIRExpression> arguments, Context context) {
        throw new NullPointerException("cant find path " + String.join(".", names));
    }

    @Override
    public Expression dotNotation(String id, Context context) {
        var names = new ArrayList<>(this.names);
        names.add(id);
        var name = String.join(".", names);
        try {
            Class<?> aClass = Class.forName(name);
            return new JVMClass(aClass);
        } catch (ClassNotFoundException e) {
            return new Path(names);
        }
    }


    @Override
    public Type asAticType() {
        return Primitive.VOID;
    }
}
