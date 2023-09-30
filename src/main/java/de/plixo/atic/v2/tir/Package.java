package de.plixo.atic.v2.tir;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class Package implements CompileRoot {
    @Getter
    private final String localName;

    @Nullable final Package parent;


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
    public List<Unit> listUnits() {
        var list = new ArrayList<>(units);
        packages.forEach(ref -> list.addAll(ref.listUnits()));
        return list;
    }

    public void addPackage(Package packages) {
        this.packages.add(packages);
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

}
