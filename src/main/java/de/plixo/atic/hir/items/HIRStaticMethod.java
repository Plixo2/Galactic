package de.plixo.atic.hir.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class HIRStaticMethod implements HIRItem {
    private final HIRMethod hirMethod;

}
