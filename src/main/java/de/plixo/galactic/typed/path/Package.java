package de.plixo.galactic.typed.path;

import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.expressions.Expression;
import de.plixo.galactic.typed.expressions.StellaPackageExpression;
import de.plixo.galactic.typed.expressions.UnitExpression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Package of code (a folder)
 */
@RequiredArgsConstructor
public final class Package implements CompileRoot {
    @Getter
    private final String localName;

    private @Nullable
    final Package parent;

    @Getter
    private final List<Unit> units = new ArrayList<>();
    @Getter
    private final List<Package> packages = new ArrayList<>();


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
    public List<Unit> getUnits() {
        var list = new ArrayList<>(units);
        packages.forEach(ref -> list.addAll(ref.getUnits()));
        return list;
    }

    @Override
    public PathElement toPathElement() {
        return new PathElement.PackageElement(this);
    }

    public void addPackage(Package packages) {
        this.packages.add(packages);
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public @Nullable Expression getDotNotation(Region region, String name) {
        for (Package aPackage : packages) {
            if (aPackage.localName().equals(name)) {
                return new StellaPackageExpression(region, aPackage);
            }
        }
        for (Unit unit : units) {
            if (unit.localName().equals(name)) {
                return new UnitExpression(region, unit);
            }
        }
        return null;
    }
}
