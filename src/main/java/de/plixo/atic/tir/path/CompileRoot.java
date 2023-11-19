package de.plixo.atic.tir.path;

import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.path.Package;
import de.plixo.atic.tir.path.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface CompileRoot permits Package, Unit {

    String localName();
    String name();

    ObjectPath toObjectPath();
    List<Unit> listUnits();
    @Nullable PathElement locate(String name);
}
