package de.plixo.galactic.tir.path;

import de.plixo.galactic.tir.ObjectPath;

import java.util.List;

/**
 * Root of the compilation.
 * It is either a package or a unit.
 */
public sealed interface CompileRoot permits Package, Unit {
    String localName();
    String name();
    ObjectPath toObjectPath();
    List<Unit> getUnits();
    PathElement toPathElement();
}
