package de.plixo.galactic.types;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.List;

@AllArgsConstructor
@Getter
public class Method {
    private final int modifier;
    private final String name;
    private final Type returnType;
    private final List<Type> arguments;
    private final MethodOwner owner;

    @Override
    public String toString() {
        return "Method " + name + "(" + getDescriptor() + ")";
    }

    public boolean isCallable(List<Type> typeList, Context context) {
        if (typeList.size() != arguments.size()) {
            return false;
        }
        for (int i = 0; i < typeList.size(); i++) {
            var argument = typeList.get(i);
            var parameter = arguments.get(i);

            if (!Type.isAssignableFrom(parameter, argument, context)) {
                return false;
            }
        }
        return true;
    }

    public String getDescriptor() {
        var builder = new StringBuilder();
        for (var argument : arguments) {
            builder.append(argument.getDescriptor());
        }
        return "(" + builder + ")" + returnType.getDescriptor();
    }


    public boolean isStatic() {
        return Modifier.isStatic(modifier);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifier);
    }

}
