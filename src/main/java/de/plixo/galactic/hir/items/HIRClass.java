package de.plixo.galactic.hir.items;

import de.plixo.galactic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRClass implements HIRItem {
    private final String className;
    private final @Nullable HIRType superClass;
    private final List<HIRType> interfaces;
    private final List<HIRField> fields;
    private final List<HIRMethod> methods;
}
