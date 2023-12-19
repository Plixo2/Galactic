package de.plixo.galactic.tir;

import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.path.Unit;

/**
 * Context for types with hints
 */
public class TypeContext extends Context {

    public TypeContext(Unit unit, CompileRoot root, LoadedBytecode loadedBytecode) {
        super(unit, root, loadedBytecode);
    }
}
