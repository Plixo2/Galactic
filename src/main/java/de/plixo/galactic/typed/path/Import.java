package de.plixo.galactic.typed.path;

import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;

public sealed interface Import {

    String alias();

    record ClassImport(String alias, Class importedClass) implements Import {

    }
    record StaticMethodImport(String alias, StellaMethod method) implements Import {

    }
}
