package de.plixo.galactic.types;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Represents a Method. Can be a static method of a Unit, or a method of a class.
 */
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
        return STR."Method \{name}(\{getDescriptor()})";
    }

    public boolean matchSignature(Signature signature, Context context) {
        if (signature.returnType() != null) {
            if (!Type.isAssignableFrom(signature.returnType(), this.returnType, context)) {
                return false;
            }
        }
        var typeList = signature.arguments();
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
        return STR."(\{builder.toString()})\{returnType.getDescriptor()}";
    }


    public boolean isStatic() {
        return Modifier.isStatic(modifier);
    }

    public boolean isPublic() {
        return Modifier.isPublic(modifier);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifier);
    }

    public static boolean signatureMatch(Method method, Method otherMethod, Context context) {
        if (!method.name().equals(otherMethod.name())) {
            return false;
        }
        if (method.arguments().size() != otherMethod.arguments().size()) {
            return false;
        }
        for (int i = 0; i < method.arguments().size(); i++) {
            var aType = method.arguments().get(i);
            var stellaSide = otherMethod.arguments().get(i);
            if (!Type.isSame(aType, stellaSide)) {
                return false;
            }
        }
        return Type.isSame(method.returnType(), otherMethod.returnType());
    }
}
