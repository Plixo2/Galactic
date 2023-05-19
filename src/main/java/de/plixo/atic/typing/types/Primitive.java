package de.plixo.atic.typing.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public final class Primitive extends Type {

    public static final Primitive BOOL = new Primitive(PrimitiveType.BOOL);
    public static final Primitive VOID = new Primitive(PrimitiveType.VOID);
    public static final Primitive INT = new Primitive(PrimitiveType.INT);
    public static final Primitive FLOAT = new Primitive(PrimitiveType.FLOAT);
    public static final Primitive STRING = new Primitive(PrimitiveType.STRING);

    @Getter
    private final PrimitiveType type;

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

    @Override
    public String shortString() {
        return string();
    }


    @AllArgsConstructor
    public enum PrimitiveType {
        INT("int"),FLOAT("float"),BOOL("bool"), STRING("string"),VOID("void");

        @Getter
        private final String id;
    }
}
