package de.plixo.typesys.types;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class FunctionType extends Type {

    @Getter
    @Nullable
    private final Type returnType;

    @Getter
    private final List<Type> arguments;

    public FunctionType(@Nullable Type returnType, List<Type> arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public String string() {
        var collect =
                arguments.stream().map(Type::string).collect(Collectors.joining(".", "(", ")"));
        return "fn " + collect + " ->" + returnType;
    }
}
