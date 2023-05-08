package de.plixo.tir.building;

import de.plixo.hir.item.HIRStruct;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.Type;

import java.util.Objects;

public class StructBuilder {
    public static Unit.Structure build(Unit unit, HIRStruct item) {
        var structure = new Unit.Structure(unit, item.name);
        item.generics.forEach(structure::addGenerics);
        structure.addTodo(item);
        return structure;
    }


    public static void addFields(Unit unit, Unit.Structure structure) {
        var item = Objects.requireNonNull(structure.todo());
        item.fields.forEach(ref -> {
            switch (ref.defineType()) {
                case NOTHING -> {
                    throw new NullPointerException("TODO");
                }
                case TYPE -> {
                    assert ref.typeHint() != null;
                    var build = TypeBuilder.build(ref.typeHint(), unit);
                    structure.addField(ref.name(), build);
                }
                case VALUE -> {
                    throw new NullPointerException("TODO");
                }
                case TYPE_VALUE -> {
                    throw new NullPointerException("TODO");
                }
            }
        });
    }
}
