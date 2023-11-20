package de.plixo.atic.tir.path;

import de.plixo.atic.files.PathEntity;
import de.plixo.atic.hir.items.HIRItem;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.types.MethodOwner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class Unit implements CompileRoot, PathElement, MethodOwner {
    @Nullable
    final Package parent;
    @Getter
    final String localName;

    @Getter
    private final PathEntity.PathUnit pathUnit;

    @Getter
    private final List<AticClass> classes = new ArrayList<>();
    private final List<AticMethod> staticMethods = new ArrayList<>();

    @Setter
    @Getter
    private List<HIRItem> hirItems = new ArrayList<>();

    public void addClass(AticClass aticClass) {
        this.classes.add(aticClass);
    }

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
    public List<Unit> listUnits() {
        return List.of(this);
    }

    @Override
    public @Nullable PathElement locate(String name) {
        for (var aClass : classes) {
            if (aClass.className().equals(name)) {
                return aClass;
            }
        }
        return null;
    }

    public @Nullable AticClass locateClass(ObjectPath path, Context context) {
        CompileRoot prev = context.root();
        var iterator = path.names().iterator();
        while (iterator.hasNext()) {
            var name = iterator.next();
            var located = prev.locate(name);
            switch (located) {
                case Unit unit -> {
                    prev = unit;
                }
                case Package aPackage -> {
                    prev = aPackage;
                }
                case AticClass aticClass -> {
                    if (iterator.hasNext()) {
                        return null;
                    }
                    return aticClass;
                }
                case null -> {
                    return null;
                }
                default -> throw new IllegalStateException("Unexpected value: " + located);
            }
        }
        return null;
    }


}