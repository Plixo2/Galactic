package de.plixo.tir.tree;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Package {

    @Getter
    private @Nullable Package parent;

    @Getter
    private String localName;

    public Package(@Nullable Package parent, String localName) {
        this.parent = parent;
        this.localName = localName;
    }

    private Map<String,Unit> units = new HashMap<>();

    private Map<String, Package> packages = new HashMap<>();


    public void addPackage(Package aPackage) {
        packages.put(aPackage.localName(), aPackage);
    }

    public void addUnit(Unit unit) {
        units.put(unit.localName(),unit);
    }

    public List<Unit> flatUnits() {
        var objects = new ArrayList<>(units.values());
        packages.forEach((k,v) -> objects.addAll(v.flatUnits()));
        return objects;
    }

    public String absolutName() {
        if (parent == null) {
            return localName;
        }
        return parent.absolutName() + "." + localName;
    }

    public @Nullable Package getPackage(String name) {
        return packages.get(name);
    }

    public @Nullable Unit getUnit(String name) {
        return units.get(name);
    }

    public List<Unit> units() {
        return new ArrayList<>(units.values());
    }

    public List<Package> packages() {
        return new ArrayList<>(packages.values());
    }

    @Override
    public String toString() {
        return "Package " + absolutName();
    }
}
