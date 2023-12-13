package de.plixo.atic.tir;

import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Unit;

/**
 * Context for types with hints
 */
public class TypeContext extends Context {

    public TypeContext(Unit unit, CompileRoot root) {
        super(unit, root);
    }
}
