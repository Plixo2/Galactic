package de.plixo.atic.types;

import de.plixo.atic.tir.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class Method {
    private final int modifier;
    private final String name;
    private final Type returnType;
    private final List<Type> arguments;
    private final Class owner;

    @Override
    public String toString() {
        return "Method{" + "modifier=" + modifier + ", name='" + name + '\'' + ", returnType=" +
                returnType + ", arguments=" + arguments + ", owner=" + owner + '}';
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
