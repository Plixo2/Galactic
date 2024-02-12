package de.plixo.galactic.typed.path;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;

public sealed interface Import {

    String alias();

    Region region();

    boolean isUserDefined();

    record ClassImport(Region region, String alias, Class importedClass, boolean isUserDefined)
            implements Import {

    }

    record StaticMethodImport(Region region, String alias, StellaMethod method,
                              boolean isUserDefined) implements Import {

    }
}
