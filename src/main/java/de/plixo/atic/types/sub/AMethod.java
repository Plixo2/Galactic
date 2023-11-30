package de.plixo.atic.types.sub;

import de.plixo.atic.tir.Context;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.MethodOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class AMethod {
    private final int modifier;
    private final String name;
    private final AType returnType;
    private final List<AType> arguments;
    private final MethodOwner owner;

    @Override
    public String toString() {
        return "AMethod{" + "modifier=" + modifier + ", name='" + name + '\'' + ", returnType=" +
                returnType + ", arguments=" + arguments + ", owner=" + owner + '}';
    }

    public boolean isCallable(List<AType> typeList, Context context) {
        if (typeList.size() != arguments.size()) {
            return false;
        }
        for (int i = 0; i < typeList.size(); i++) {
            var argument = typeList.get(i);
            var parameter = arguments.get(i);

            if (!AType.isAssignableFrom(parameter, argument, context)) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AMethod aMethod = (AMethod) o;
        return modifier == aMethod.modifier && Objects.equals(name, aMethod.name) &&
                Objects.equals(returnType, aMethod.returnType) &&
                Objects.equals(arguments, aMethod.arguments) &&
                Objects.equals(owner, aMethod.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifier, name, returnType, arguments, owner);
    }
}
