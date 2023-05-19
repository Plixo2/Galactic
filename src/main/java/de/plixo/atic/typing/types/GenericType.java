package de.plixo.atic.typing.types;

import lombok.Getter;

public final class GenericType extends Type{

    @Getter
    private final String name;

    public GenericType(String name) {
        this.name = name;
    }

    @Override
    public String string() {
        return "<" + name + ">";
    }

    @Override
    public String shortString() {
        return string();
    }
}
