package de.plixo.galactic.tir.stellaclass;

import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.types.Class;

public sealed interface MethodOwner {
    record UnitOwner(Unit unit) implements MethodOwner {
    }
    record ClassOwner(Class aClass) implements MethodOwner {
    }
}
