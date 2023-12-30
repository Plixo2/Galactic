package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Type;

import java.util.List;

public record InstanceCreationExpression(Region region, Method constructor, Class type,
                                         List<Expression> expressions) implements Expression {

    @Override
    public Type getType(Context context) {
        return type;
    }
}
