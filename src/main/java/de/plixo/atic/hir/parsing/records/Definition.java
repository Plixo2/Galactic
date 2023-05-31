package de.plixo.atic.hir.parsing.records;

import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.hir.parsing.HIRExprParser;
import de.plixo.atic.hir.parsing.HIRTypeParser;
import de.plixo.atic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record Definition(Region region, String name, @Nullable HIRType typehint,
                         @Nullable HIRExpr expression) {


    public static Definition create(Node node) {
        var name = node.getID();
        HIRType type = null;
        HIRExpr expr = null;
        var typeHintOptNode = node.get("typeHintOpt");
        var defaultParamOptNode = node.get("defaultParamOpt");
        if (typeHintOptNode.has("type")) {
            type = HIRTypeParser.parse(typeHintOptNode.get("type"));
        }
        if (defaultParamOptNode.has("expression")) {
            expr = HIRExprParser.parse(defaultParamOptNode.get("expression"));
        }
        return new Definition(node.region(), name, type, expr);
    }
}
