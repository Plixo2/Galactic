package de.plixo.galactic.tir.expressions;

import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record StringExpression(Region region, String value) implements Expression {
    @Override
    public Type getType(Context context) {
        var objectPath = new ObjectPath("java", "lang", "String");
        var stringClass = JVMLoader.asJVMClass(objectPath, context.loadedBytecode());
        if (stringClass == null) {
            throw new FlairException("Cant find loaded java.lang.String");
        }
        return stringClass;
    }
}
