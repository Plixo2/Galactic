package de.plixo.typesys.types;


import de.plixo.tir.tree.Unit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StructImplementation extends Type {
    public Unit.Structure struct;

    public Map<GenericType, Type> implementation = new LinkedHashMap<>();

    public StructImplementation(Unit.Structure struct) {
        this.struct = struct;
    }

    public List<Type> getTypes() {
        return struct.fields().keySet().stream().map(this::get).toList();
    }

    public Type get(String memberName) {
        var type = struct.getField(memberName);
        if (type == null) {
            return null;
        }

        if(type instanceof SolvableType solvableType) {
            throw new NullPointerException("TODO");
        }

        return switch (type) {
            case Primitive primitive -> type;
            case StructImplementation subImpl -> subImpl.newType(implementation);
            case GenericType genericType -> implementation.get(genericType);
            case null, default -> throw new NullPointerException("Unknown " + memberName);
        };
    }

    public StructImplementation newType(Map<GenericType, Type> implementation) {
        var structImplementation = new StructImplementation(struct);
        this.implementation.forEach((generic, impl) -> {
            structImplementation.implementation.put(generic, implementation.getOrDefault(impl, impl));
        });
        return structImplementation;
    }

    @Override
    public String string() {
        var bob = new StringBuilder();
        bob.append("StructImpl of ").append(struct.absolutName());

        bob.append(" <");
        for (GenericType genericType : this.struct.generics()) {
            bob.append(genericType.name);
        }
        bob.append("> ");

        bob.append(" impl [");
        this.implementation.forEach((original, impl) -> {
            bob.append(original).append(" -> ").append(impl).append(", ");
        });
        bob.append("] {");
        bob.append("\n");

        var iter = this.struct.fields().entrySet().iterator();
        while (iter.hasNext()) {
            var next = iter.next();
            var key = next.getKey();
            var type = next.getValue();
            bob.append(key).append(": ").append(type.string());
            bob.append(" -> ");
            bob.append(get(key));

            bob.append("\n");
        }

        bob.append("}");

        return bob.toString();
    }

    public void implement(GenericType genericType, Type type) {
        //overwrites the implementation;
        this.implementation.put(genericType, type);
    }

}
