package de.plixo.atic.tir.aticclass;

import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;

public sealed interface MethodOwner {
    record UnitOwner(Unit unit) implements MethodOwner {
    }
    record ClassOwner(Class aClass) implements MethodOwner {
    }
}
