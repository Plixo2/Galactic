package de.plixo.atic.v2.tir;

import java.util.List;

public sealed interface CompileRoot permits Package, Unit {

    String localName();
    String name();

    List<Unit> listUnits();
}
