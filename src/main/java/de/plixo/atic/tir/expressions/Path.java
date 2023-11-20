package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Path extends Expression {
    private final ObjectPath path;

    @Override
    public Expression dotNotation(String id, Context context) {
        var next = path.add(id);
        var theClass = context.getClass(next);
        if (theClass == null) {
            return new Path(next);
        } else {
            return new ClassExpression(theClass);
        }
    }

    @Override
    public AType getType() {
        return new AVoid();
    }
}