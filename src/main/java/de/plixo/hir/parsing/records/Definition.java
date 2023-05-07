package de.plixo.hir.parsing.records;

import de.plixo.atic.lexer.Node;
import de.plixo.hir.expr.HIRExpr;
import de.plixo.hir.parsing.HIRExprParser;
import de.plixo.hir.parsing.HIRTypeParser;
import de.plixo.hir.typedef.HIRType;
import org.jetbrains.annotations.Nullable;

public record Definition(String name, @Nullable HIRType typehint, @Nullable HIRExpr expression) {


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
        return new Definition(name, type, expr);
    }
}
