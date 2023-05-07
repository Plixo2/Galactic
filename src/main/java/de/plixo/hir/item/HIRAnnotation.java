package de.plixo.hir.item;

import de.plixo.hir.expr.HIRExpr;

import java.util.List;

public class HIRAnnotation {
    public String name;

    List<HIRExpr> arguments;

    public HIRAnnotation(String name, List<HIRExpr> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
}
