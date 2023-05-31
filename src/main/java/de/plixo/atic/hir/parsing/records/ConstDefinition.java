package de.plixo.atic.hir.parsing.records;

import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.hir.parsing.HIRExprParser;
import de.plixo.atic.hir.parsing.HIRTypeParser;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public record ConstDefinition(Region region, String name, @Nullable HIRType typehint,
                              HIRExpr expression) {

    public static ConstDefinition create(Node node) {
        var name = node.getID();
        HIRType type = null;
        var varTypeDefNode = node.get("constTypeDef");
        if (varTypeDefNode.has("type")) {
            type = HIRTypeParser.parse(varTypeDefNode.get("type"));
        }
        var expr = HIRExprParser.parse(varTypeDefNode.get("expression"));
        return new ConstDefinition(node.region(), name, type, expr);
    }
}
