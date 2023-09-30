package de.plixo.atic.v2.hir2.utils;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.v2.hir2.HIRExpressionParsing;
import de.plixo.atic.v2.hir2.expressions.HIRExpression;

import java.util.List;

public class ExpressionCommaList {
    public static List<HIRExpression> toList(Node node) {
        var list = node.list("expressionList", "expressionListOpt", "expression");
        return list.stream().map(HIRExpressionParsing::parse).toList();
    }
}
