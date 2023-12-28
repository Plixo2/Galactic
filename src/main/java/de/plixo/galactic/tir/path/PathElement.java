package de.plixo.galactic.tir.path;

import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import org.jetbrains.annotations.Nullable;

/**
 * A PathElement is a part of a path.
 * Atic Class, Package or Unit
 */
public sealed interface PathElement {

    default @Nullable PathElement get(ObjectPath path) {
        var element = this;
        for (String name : path.names()) {
            if (element == null) {
                return null;
            }
            element = element.next(name);
        }
        return element;
    }

    @Nullable PathElement next(String name);

    record UnitElement(Unit unit) implements PathElement {

        @Override
        public @Nullable PathElement next(String name) {
            for (var aClass : unit.classes()) {
                if (aClass.localName().equals(name)) {
                    return new StellaClassElement(aClass);
                }
            }
            for (var method : unit.staticMethods()) {
                if (method.localName().equals(name)) {
                    return new StellaMethodElement(method);
                }
            }
            return null;
        }
    }

    record PackageElement(Package aPackage) implements PathElement {

        @Override
        public @Nullable PathElement next(String name) {
            for (Unit unit : aPackage.units()) {
                if (unit.localName().equals(name)) {
                    return new UnitElement(unit);
                }
            }
            for (var aPackage : aPackage.packages()) {
                if (aPackage.localName().equals(name)) {
                    return new PackageElement(aPackage);
                }
            }
            return null;
        }
    }

    record StellaClassElement(StellaClass stellaClass) implements PathElement {

        @Override
        public @Nullable PathElement next(String name) {
            return null;
        }
    }
    record StellaMethodElement(StellaMethod method) implements PathElement {

        @Override
        public @Nullable PathElement next(String name) {
            return null;
        }
    }
}
