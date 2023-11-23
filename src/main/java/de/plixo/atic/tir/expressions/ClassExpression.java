package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.tir.Context;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Modifier;

@RequiredArgsConstructor
public final class ClassExpression extends Expression {
    private final AClass aClass;

    @Override
    public Expression dotNotation(String id, Context context) {
        var field = aClass.getField(id, context);
        if (field != null) {
            if (!Modifier.isStatic(field.modifier) || !Modifier.isPublic(field.modifier)) {
                throw new NullPointerException("field not public and static");
            }
            return new StaticFieldExpression(aClass, field);
        }
        var method = aClass.getMethods(id, context);
        var filtered = method.filter(
                ref -> Modifier.isStatic(ref.modifier) && Modifier.isPublic(ref.modifier));
        if (filtered.isEmpty()) {
            throw new NullPointerException("cant find field or public & static method " + id);
        }
        return new StaticMethodExpression(aClass, filtered);

    }

    @Override
    public AType getType() {
        return new AVoid();
    }
}
