package de.plixo.typesys.types;

public final class GenericType extends Type{

    String name;

    public GenericType(String name) {
        this.name = name;
    }

    @Override
    public String string() {
        return "<" + name + ">";
    }
}
