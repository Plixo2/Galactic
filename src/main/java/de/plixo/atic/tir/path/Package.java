package de.plixo.atic.tir.path;

import de.plixo.atic.tir.ObjectPath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class Package implements CompileRoot, PathElement {
    @Getter
    private final String localName;

    private @Nullable final Package parent;

    List<Unit> units = new ArrayList<>();
    List<Package> packages = new ArrayList<>();


    @Override
    public String name() {
        if (parent == null) {
            return localName;
        }
        return parent.name() + "." + localName;
    }

    @Override
    public ObjectPath toObjectPath() {
        if (parent == null) {
            return new ObjectPath(localName);
        }
        return parent.toObjectPath().add(localName);
    }

    @Override
    public List<Unit> flatUnits() {
        var list = new ArrayList<>(units);
        packages.forEach(ref -> list.addAll(ref.flatUnits()));
        return list;
    }

    public void addPackage(Package packages) {
        this.packages.add(packages);
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    @Override
    public @Nullable PathElement locate(String name) {
        for (Package aPackage : packages) {
            if (aPackage.localName().equals(name)) {
                return aPackage;
            }
        }
        for (Unit unit : units) {
            if (unit.localName().equals(name)) {
                return unit;
            }
        }
        return null;
    }
}
