package de.plixo.atic.hir.item;

import de.plixo.atic.hir.expr.HIRExpr;
import lombok.Getter;

import java.util.List;

public class HIRAnnotation {
    @Getter
    private final String name;

    @Getter
    private final List<HIRExpr> arguments;

    public HIRAnnotation(String name, List<HIRExpr> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
}
