package de.plixo.tir.building;

import de.plixo.atic.path.PathUnit;
import de.plixo.hir.item.HIRItem;
import de.plixo.hir.item.HIRStruct;
import de.plixo.tir.tree.Package;
import de.plixo.tir.tree.Unit;

import java.util.List;

public class UnitBuilder {
    public static Unit build(Package parent, PathUnit pathUnit, List<HIRItem> items) {
        var unit = new Unit(parent, pathUnit.localName());

        for (var item : items) {
            if (item instanceof HIRStruct hirStruct) {
                unit.addStructure(StructBuilder.build(unit, hirStruct));
            } else {
                unit.addTodo(item);
            }
        }

        return unit;
    }

    public static void addImports(Unit unit, Package root) {
        var imports = unit.getAndRemoveImports();
        imports.forEach(ref -> ImportBuilder.build(ref, root).forEach(unit::addImport));
    }

    public static void addConstants(Unit unit) {
        var constants = unit.getAndRemoveConstants();
        constants.forEach(ref -> unit.addConstant(ConstantBuilder.build(ref, unit)));
    }

    public static void addConstantExpressions(Unit unit) {
        unit.constants().forEach(ref -> ConstantBuilder.addExpressions(unit, ref));
    }

    public static void addFields(Unit unit) {
        unit.structs().forEach(ref -> StructBuilder.addFields(unit, ref));
    }
}
