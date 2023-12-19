package de.plixo.galactic.hir.utils;

import de.plixo.galactic.hir.HIRExpressionParsing;
import de.plixo.galactic.hir.expressions.HIRExpression;
import de.plixo.galactic.parsing.Node;

import java.util.List;

public class ExpressionCommaList {
    public static List<HIRExpression> toList(Node node) {
        var list = node.list("expressionList", "expressionListOpt", "expression");
        return list.stream().map(HIRExpressionParsing::parse).toList();
    }
}
