package de.plixo.atic.tir.building;

import de.plixo.atic.exceptions.reasons.GeneralFailure;
import de.plixo.atic.exceptions.reasons.ImportFailure;
import de.plixo.atic.hir.item.HIRImport;
import de.plixo.atic.tir.tree.Import;
import de.plixo.atic.tir.tree.Package;
import de.plixo.atic.tir.tree.Unit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportBuilder {

    /**
     * bad impl
     */
    public static List<Import> build(HIRImport aImport, Unit parentUnit, Package root) {
        var queue = new ArrayDeque<>(aImport.path());
        if (aImport.useJVMInterface()) {
            if (aImport.importAll()) {
                throw new GeneralFailure(aImport.region(),
                        "can import only one interface").create();
            }
            var join = String.join(".", aImport.path());
//            var classLoader = ClassLoader.getSystemClassLoader();
//            var resource = classLoader.getResourceAsStream(join);
            try {
                var jvmClass = Class.forName(join);
                if (!jvmClass.isInterface()) {
                    throw new GeneralFailure(aImport.region(),
                            "class is not an interface").create();
                }
                return Collections.singletonList(new Import.JVMInterface(jvmClass));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        var currentPackage = root;
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
            if (aImport.importAll()) {
                return root.units().stream().map(ref -> (Import) new Import.UnitImport(ref))
                        .toList();
            } else {
                throw new ImportFailure(aImport.region(),
                        ImportFailure.ImportFailType.IMPORT_PACKAGE,
                        currentPackage.absolutName()).create();
            }
        } else {
            var unitName = queue.poll();
            var unit = currentPackage.getUnit(unitName);
            if (unit == null) {
                throw new ImportFailure(aImport.region(), ImportFailure.ImportFailType.UNKNOWN_UNIT,
                        unitName).create();
            }
            if (queue.isEmpty()) {
                if (aImport.importAll()) {
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
                if (queue.isEmpty() && !aImport.importAll()) {
                    return List.of(new Import.StructureImport(structure));
                }
                throw new ImportFailure(aImport.region(), ImportFailure.ImportFailType.IMPORT_FIELD,
                        termName).create();
            } else {
                var constant = unit.getConstant(termName);
                if (constant != null) {
                    if (queue.isEmpty() && !aImport.importAll()) {
                        return List.of(new Import.ConstantImport(constant));
                    }
                    throw new ImportFailure(aImport.region(),
                            ImportFailure.ImportFailType.IMPORT_FIELD, termName).create();
                } else {
                    throw new ImportFailure(aImport.region(),
                            ImportFailure.ImportFailType.UNKNOWN_OBJECT, termName).create();
                }
            }

        }


    }
}
