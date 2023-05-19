package de.plixo.atic.tir.building;

import de.plixo.atic.files.PathEntity;
import de.plixo.atic.hir.item.HIRItem;
import de.plixo.atic.tir.tree.Package;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TreeBuilder {

    public static Package build(PathEntity projectPath,
                                Map<PathEntity.PathUnit, List<HIRItem>> hirLookUp) {
        return switch (projectPath) {
            case PathEntity.PathUnit pathUnit -> {
                var root = new Package(null, "root");
                root.addUnit(UnitBuilder.build(root, pathUnit,
                        Objects.requireNonNull(hirLookUp.get(pathUnit))));
                yield root;
            }
            case PathEntity.PathDir dir -> createPackage(null, dir, hirLookUp);
        };
    }

    private static Package createPackage(Package parent, PathEntity.PathDir dir,
                                         Map<PathEntity.PathUnit, List<HIRItem>> hirLookUp) {
        var aPackage = new Package(parent, dir.localName());
        dir.subDirs().forEach(ref -> aPackage.addPackage(createPackage(aPackage, ref, hirLookUp)));
        dir.units().forEach(ref -> aPackage.addUnit(
                UnitBuilder.build(aPackage, ref, Objects.requireNonNull(hirLookUp.get(ref)))));
        return aPackage;
    }

}
