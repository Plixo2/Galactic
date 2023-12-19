package de.plixo.galactic.hir.items;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class HIRStaticMethod implements HIRItem {
    private final HIRMethod hirMethod;

}
