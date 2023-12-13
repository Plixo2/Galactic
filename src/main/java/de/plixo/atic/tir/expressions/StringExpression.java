package de.plixo.atic.tir.expressions;

import de.plixo.atic.boundary.JVMLoader;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.types.Type;
import de.plixo.atic.tir.Context;
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
