package de.plixo.typesys.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class Primitive extends Type {

    public static Primitive BOOL = new Primitive(PrimitiveType.BOOL);
    public static Primitive VOID = new Primitive(PrimitiveType.VOID);
    public static Primitive INT = new Primitive(PrimitiveType.INT);
    public static Primitive FLOAT = new Primitive(PrimitiveType.FLOAT);
    public static Primitive STRING = new Primitive(PrimitiveType.STRING);

    public PrimitiveType type;

    private Primitive(PrimitiveType type) {
        this.type = type;
    }

    public static @Nullable Primitive get(String name) {
        return switch (name) {
            case "int" -> INT;
            case "float" -> FLOAT;
            case "void" -> VOID;
            case "bool" -> BOOL;
            case "string" -> STRING;
            default -> null;
        };
    }

    @Override
    public String string() {
        return type.name();
    }


    @AllArgsConstructor
    public enum PrimitiveType {
        INT("int"),FLOAT("float"),BOOL("bool"), STRING("string"),VOID("void");

        @Getter
        private final String id;
    }
}
