package de.plixo.atic.typing.types;


import de.plixo.atic.tir.tree.Unit;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class StructImplementation extends Type {
    @Getter
    private final Unit.Structure struct;

    @Getter
    private final Map<GenericType, Type> implementation = new LinkedHashMap<>();

    public StructImplementation(Unit.Structure struct) {
        this.struct = struct;
    }

    public List<Type> getTypes() {
        return new ArrayList<>(implementation.values());
       // return struct.fields().keySet().stream().map(this::get).toList();
    }

    public @Nullable Type get(String memberName) {
        var field = struct.getField(memberName);
        if (field == null) {
            return null;
        }
        var type = field.type();
        return convertType(type, implementation);
    }

    public StructImplementation newType(Map<? extends Type, Type> implementation) {
        var structImplementation = new StructImplementation(struct);
        this.implementation.forEach(
                (generic, impl) -> structImplementation.implementation.put(generic,
                        implementation.getOrDefault(impl, impl)));
        return structImplementation;
    }

    @Override
    public String string() {
        var bob = new StringBuilder();
        bob.append("StructImpl of ").append(struct.absolutName());

        bob.append(" <");
        for (GenericType genericType : this.struct.generics()) {
            bob.append(genericType.name());
        }
        bob.append("> ");

        bob.append(" impl [");
        this.implementation.forEach(
                (original, impl) -> bob.append(original).append(" -> ").append(impl).append(", "));
        bob.append("] {");
        bob.append("\n");

        for (Map.Entry<String, Unit.Structure.Field> next : this.struct.fields().entrySet()) {
            var key = next.getKey();
            var type = next.getValue();
            bob.append(key).append(": ").append(type.type().shortString());
            bob.append(" to ");
            bob.append(Objects.requireNonNull(get(key)).shortString());

            bob.append("\n");
        }

        bob.append("}");

        return bob.toString();
    }

    @Override
    public String shortString() {
        return struct.absolutName();
    }

    public void implement(GenericType genericType, Type type) {
        //overwrites the implementation;
        this.implementation.put(genericType, type);
    }



}
