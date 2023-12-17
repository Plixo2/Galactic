package de.plixo.atic.tir.path;

import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.aticclass.AticClass;
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
            for (AticClass aClass : unit.classes()) {
                if (aClass.localName().equals(name)) {
                    return new AticClassElement(aClass);
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

    record AticClassElement(AticClass aticClass) implements PathElement {

        @Override
        public @Nullable PathElement next(String name) {
            return null;
        }
    }
}
