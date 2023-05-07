package de.plixo.hir.parsing.records;

import de.plixo.atic.lexer.Node;
import de.plixo.hir.expr.HIRExpr;
import de.plixo.hir.parsing.HIRExprParser;
import de.plixo.hir.parsing.HIRTypeParser;
import de.plixo.hir.typedef.HIRType;
import org.jetbrains.annotations.Nullable;

public record VarDefinition(String name, @Nullable HIRType typehint, HIRExpr expression) {

    public static VarDefinition create(Node node) {
        var name = node.getID();
        HIRType type = null;
        var varTypeDefNode = node.get("varTypeDef");
        if (varTypeDefNode.has("type")) {
            type = HIRTypeParser.parse(varTypeDefNode.get("type"));
        }
        var expr = HIRExprParser.parse(varTypeDefNode.get("expression"));
        return new VarDefinition(name, type, expr);
    }
}
