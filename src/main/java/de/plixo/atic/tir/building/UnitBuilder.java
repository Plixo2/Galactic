package de.plixo.atic.tir.building;

import de.plixo.atic.files.PathEntity;
import de.plixo.atic.hir.item.HIRItem;
import de.plixo.atic.hir.item.HIRStruct;
import de.plixo.atic.tir.tree.Package;
import de.plixo.atic.tir.tree.Unit;

import java.util.List;

public class UnitBuilder {
    public static Unit build(Package parent, PathEntity.PathUnit pathUnit, List<HIRItem> items) {
        var unit = new Unit(pathUnit.file(),parent, pathUnit.localName());

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
        imports.forEach(ref -> ImportBuilder.build(ref, unit, root).forEach(unit::addImport));
    }

    public static void addConstants(Unit unit) {
        var constants = unit.getAndRemoveConstants();
        constants.forEach(ref -> unit.addConstant(ConstantBuilder.build(ref, unit)));
    }

    public static void addConstantExpressions(Unit unit) {
        unit.constants().forEach(ref -> ConstantBuilder.addExpressions(unit, ref));
    }

    public static void addAnnotations(Unit unit) {
        unit.structs().forEach(ref -> StructBuilder.addAnnotations(unit, ref));
    }

    public static void addFields(Unit unit) {
        unit.structs().forEach(ref -> StructBuilder.addFields(unit, ref));
    }

    public static void addDefaults(Unit unit) {
        unit.structs().forEach(ref -> StructBuilder.addDefaults(unit, ref));
    }

}
