package de.plixo.atic.tir.path;

import de.plixo.atic.tir.ObjectPath;
import org.jetbrains.annotations.Nullable;

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
    @Nullable PathElement locate(String name);
}
