package de.plixo.galactic.high_level.items;

import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRClass implements HIRItem {
    private final Region region;
    private final String className;
    private final @Nullable HIRType superClass;
    private final List<HIRType> interfaces;
    private final List<HIRField> fields;
    private final List<HIRMethod> methods;
}
