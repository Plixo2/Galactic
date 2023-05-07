package de.plixo.tir.building;

import de.plixo.hir.item.HIRStruct;
import de.plixo.tir.tree.Unit;

public class StructBuilder {
    public static Unit.Structure build(Unit unit, HIRStruct item) {
        var structure = new Unit.Structure(unit, item.name);
        item.generics.forEach(structure::addGenerics);
        return structure;
    }
}
