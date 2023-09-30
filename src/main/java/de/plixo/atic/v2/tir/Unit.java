package de.plixo.atic.v2.tir;

import de.plixo.atic.files.PathEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class Unit implements CompileRoot {
    @Nullable
    final Package parent;
    @Getter
    final String localName;

    @Getter
    final PathEntity.PathUnit pathUnit;

    @Getter
    private final List<Method> methods = new ArrayList<>();

    public void addMethod(Method method) {
        methods.add(method);
    }

    @Override
    public String name() {
        if (parent == null) {
            return localName;
        }
        return parent.name() + "." + localName;
    }

    @Override
    public List<Unit> listUnits() {
        return List.of(this);
    }

}
