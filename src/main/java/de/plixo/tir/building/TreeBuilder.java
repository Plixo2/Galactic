package de.plixo.tir.building;

import de.plixo.atic.path.PathDir;
import de.plixo.atic.path.PathEntity;
import de.plixo.atic.path.PathUnit;
import de.plixo.hir.item.HIRItem;
import de.plixo.tir.tree.Package;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TreeBuilder {

    public static Package build(PathEntity projectPath, Map<PathUnit, List<HIRItem>> hirLookUp) {
        return switch (projectPath) {
            case PathUnit pathUnit -> throw new NullPointerException("TODO top main unit");
            case PathDir dir -> createPackage(null, dir, hirLookUp);
        };
    }

    private static Package createPackage(Package parent, PathDir dir,
                                  Map<PathUnit, List<HIRItem>> hirLookUp) {
        var aPackage = new Package(parent, dir.localName());
        dir.subDirs().forEach(ref -> aPackage.addPackage(createPackage(aPackage, ref, hirLookUp)));
        dir.units().forEach(ref -> aPackage.addUnit(
                UnitBuilder.build(aPackage, ref, Objects.requireNonNull(hirLookUp.get(ref)))));
        return aPackage;
    }

}
