package de.plixo.atic.hir.utils;

import de.plixo.atic.hir.HIRExpressionParsing;
import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.lexer.Node;

import java.util.List;

public class ExpressionCommaList {
    public static List<HIRExpression> toList(Node node) {
        var list = node.list("expressionList", "expressionListOpt", "expression");
        return list.stream().map(HIRExpressionParsing::parse).toList();
    }
}
