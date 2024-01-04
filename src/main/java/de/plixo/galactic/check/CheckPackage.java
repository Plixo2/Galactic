package de.plixo.galactic.check;

import de.plixo.galactic.Universe;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.Package;

import java.util.HashSet;

public class CheckPackage {
    public void check(Package thePackage, CompileRoot root, Universe language,
                      CheckProject checkProject) {
        if (!CheckProject.isAllowedTopLevelName(thePackage.localName())) {
            throw new FlairException(STR."Package name \{thePackage.localName()} is not allowed");
        }
        var names = new HashSet<String>();
        for (var unit : thePackage.units()) {
            if (!names.add(unit.localName())) {
                throw new FlairException(STR."Duplicate name for unit \{unit.localName()}");
            }
            checkProject.checkUnit().check(unit, root, language, checkProject);
        }
        for (var subPackage : thePackage.packages()) {
            if (!names.add(subPackage.localName())) {
                throw new FlairException(STR."Duplicate name for package \{subPackage.localName()}");
            }
            check(subPackage, root, language, checkProject);
        }

    }
}
