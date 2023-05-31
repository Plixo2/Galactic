package de.plixo.atic.hir.item;

import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

import java.util.List;

public class HIRAnnotation {
    @Getter
    private final Region region;
    @Getter
    private final String name;

    @Getter
    private final List<HIRExpr> arguments;

    public HIRAnnotation(Region region, String name, List<HIRExpr> arguments) {
        this.region = region;
        this.name = name;
        this.arguments = arguments;
    }
}
