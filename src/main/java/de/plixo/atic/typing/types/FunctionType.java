package de.plixo.atic.typing.types;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FunctionType extends Type {

    @Getter
    private final Type returnType;

    @Getter
    private final List<Type> arguments;

    @Getter
    private final Type owner;

    public FunctionType(Type returnType, List<Type> arguments, Type owner) {
        this.returnType = returnType;
        this.arguments = arguments;
        this.owner = Objects.requireNonNull(owner);
    }


    @Override
    public String string() {
        var collect = arguments.stream().map(Type::shortString)
                .collect(Collectors.joining(".", "(", ")"));
        var owner = "[" + owner().shortString() + "] ";
        return "fn " + owner + collect + " -> " + returnType.shortString();
    }

    @Override
    public String shortString() {
        return string();
    }

    private String shortDec(Type type) {
        return switch (type) {
            case FunctionType functionType -> "fn";
            case GenericType genericType -> "<>";
            case Primitive primitive -> primitive.string();
            case SolvableType solvableType -> "?";
            case StructImplementation structImplementation ->
                    structImplementation.struct().absolutName();
        };
    }
}
