package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class StringExpression extends Expression {

    private final String value;

    @Override
    public Type getType(Context context) {
        var objectPath = new ObjectPath("java", "lang", "String");
        var stringClass = JVMLoader.asJVMClass(objectPath, context.loadedBytecode());
        if (stringClass == null) {
            throw new NullPointerException("cant find java.lang.Object");
        }
        return stringClass;
    }
}
