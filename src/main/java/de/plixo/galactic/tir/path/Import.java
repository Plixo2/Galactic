package de.plixo.galactic.tir.path;

import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Method;

public sealed interface Import {

    String alias();

    record ClassImport(String alias, Class importedClass) implements Import {

    }
    record StaticMethodImport(String alias, StellaMethod method) implements Import {

    }
}
