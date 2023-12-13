package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HIRStaticMethod implements HIRItem {
    @Getter
    private final HIRMethod hirMethod;

}
