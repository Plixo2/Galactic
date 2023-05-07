package de.plixo.tir.building;

import de.plixo.hir.item.HIRImport;
import de.plixo.tir.tree.Import;
import de.plixo.tir.tree.Package;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ImportBuilder {

    /**
     * bad impl
     */
    public static List<Import> build(HIRImport aImport, Package root) {
        var queue = new ArrayDeque<>(aImport.path);

        String combined = String.join(".", aImport.path);
        Package currentPackage = root;
        while (!queue.isEmpty()) {
            var element = queue.peek();
            var subPackage = currentPackage.getPackage(element);
            if (subPackage == null) {
                break;
            }
            queue.poll();
            currentPackage = subPackage;
        }

        if (queue.isEmpty()) {
            if (aImport.importAll) {
                return root.units().stream().map(ref -> (Import) new Import.UnitImport(ref))
                        .toList();
            } else {
                throw new NullPointerException("Import only a units, or all units inside a " +
                        "package (non recursive) with \"*\"");
            }
        } else {
            var unitName = queue.poll();
            var unit = currentPackage.getUnit(unitName);
            if (unit == null) {
                throw new NullPointerException("Cant find unit " + unitName);
            }
            if (queue.isEmpty()) {
                if (aImport.importAll) {

                    var structures = unit.structs().stream()
                            .map(ref -> (Import) new Import.StructureImport(ref)).toList();
                    var constants = unit.constants().stream()
                            .map(ref -> (Import) new Import.ConstantImport(ref)).toList();
                    var imports = new ArrayList<>(constants);
                    imports.addAll(structures);
                    return imports;
                } else {
                    return List.of(new Import.UnitImport(unit));
                }
            }
            var termName = queue.poll();
            var structure = unit.getStructure(termName);
            if (structure != null) {
                if (queue.isEmpty() && !aImport.importAll) {
                    return List.of(new Import.StructureImport(structure));
                }
                throw new NullPointerException("Cant import fields in " + termName);
            } else {
                var constant = unit.getConstant(termName);
                if (constant != null) {
                    if (queue.isEmpty() && !aImport.importAll) {
                        return List.of(new Import.ConstantImport(constant));
                    }
                    throw new NullPointerException("Cant import fields in a constant");
                } else {
                    throw new NullPointerException("Cant import struct or constant in " + termName);
                }
            }

        }


    }
}
