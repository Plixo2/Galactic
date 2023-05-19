package de.plixo.atic.hir.parsing.records;

import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.hir.parsing.HIRExprParser;
import de.plixo.atic.hir.parsing.HIRTypeParser;
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
