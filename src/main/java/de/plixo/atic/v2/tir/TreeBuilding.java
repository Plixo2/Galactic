package de.plixo.atic.v2.tir;

import de.plixo.atic.Language;
import de.plixo.atic.files.PathEntity;
import org.jetbrains.annotations.Nullable;

public class TreeBuilding {

    public static CompileRoot toTree(Language.HIR2 hir2) {
        return switch (hir2.projectPath()) {
            case PathEntity.PathUnit unit -> createUnit(null, unit, hir2);
            case PathEntity.PathDir pathDir -> createPackage(null, pathDir, hir2);
        };
    }

    private static Package createPackage(@Nullable Package parent, PathEntity.PathDir pathDir,
                                         Language.HIR2 hir2) {
        var aPackage = new Package(pathDir.localName(), parent);
        var subPackages =
                pathDir.subDirs().stream().map(dir -> createPackage(aPackage, dir, hir2)).toList();

        var units = pathDir.units().stream().map(pathUnit -> createUnit(aPackage, pathUnit, hir2))
                .toList();
        subPackages.forEach(aPackage::addPackage);
        units.forEach(aPackage::addUnit);
        return aPackage;
    }

    private static Unit createUnit(@Nullable Package parent, PathEntity.PathUnit unit,
                                   Language.HIR2 hir2) {
        return new Unit(parent, unit.localName(), unit);
    }
}
