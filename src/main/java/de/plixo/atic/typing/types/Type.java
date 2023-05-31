package de.plixo.atic.typing.types;

import java.util.Map;

public sealed abstract class Type
        permits FunctionType, GenericType, Primitive, SolvableType, StructImplementation {

    public abstract String string();

    public abstract String shortString();

    @Override
    public String toString() {
        return formatted();
    }


    public String formatted() {
        var bob = new StringBuilder();
        var string = shortString();
        var level = 0;
        var iterator = string.lines().iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();

            if (line.contains("}")) level--;
            bob.append("    ".repeat(level)).append(line);
            if (iterator.hasNext()) {
                bob.append("\n");
            }
            if (line.contains("{")) level++;
        }
        return bob.toString();
    }

    public static Type convertType(Type type, Map<? extends Type, Type> implementation) {
        return switch (type) {
            case Primitive ignored -> type;
            case StructImplementation subImpl -> {
                var structImplementation = new StructImplementation(subImpl.struct());
                for (GenericType generic : subImpl.struct().generics()) {
                    structImplementation.implement(generic, convertType(generic, implementation));
                }
                subImpl.implementation().forEach(
                        (generic, impl) -> structImplementation.implementation().put(generic,
                                convertType(impl, implementation)));
                yield structImplementation;
            }
            case GenericType genericType -> implementation.get(genericType);
            case FunctionType functionType -> new FunctionType(
                    implementation.getOrDefault(functionType.returnType(),
                            functionType.returnType()),
                    functionType.arguments().stream().map(ref -> convertType(ref, implementation))
                            .toList(), convertType(functionType.owner(), implementation));
            case SolvableType solvableType -> throw new NullPointerException("");
        };
    }

    public Type unwrap() {
        if (this instanceof SolvableType solvableType) {
            if (solvableType.isSolved()) {
                return solvableType.type();
            }
        }
        return this;
    }
}
