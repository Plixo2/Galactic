package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRClass implements HIRItem {
    private final String className;
    private final HIRType superClass;
    private final List<HIRType> interfaces;
    private final List<HIRField> fields;
    private final List<HIRMethod> methods;
}
