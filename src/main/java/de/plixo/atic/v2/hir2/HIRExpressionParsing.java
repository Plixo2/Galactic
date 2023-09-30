package de.plixo.atic.v2.hir2;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.v2.hir2.expressions.*;
import de.plixo.atic.v2.hir2.utils.ExpressionCommaList;

import java.math.BigDecimal;

public class HIRExpressionParsing {
    public static HIRExpression parse(Node node) {
        node.assertType("expression");
        if (node.has("factor")) {
            return parseFactor(node.get("factor"));
        } else if (node.has("variableDefinition")) {
            return parseVarDefinition(node.get("variableDefinition"));
        } else if (node.has("branch")) {
            return parseBranch(node.get("branch"));
        }
        throw new NullPointerException("unknown expression");
    }

    private static HIRBranch parseBranch(Node node) {
        node.assertType("branch");
        var expression = HIRExpressionParsing.parse(node.get("expression"));
        var elsePart = node.get("elsePart");
        var body = HIRExpressionParsing.parse(elsePart.get("expression"));
        return new HIRBranch(expression, body);
    }

    private static HIRExpression parseFactor(Node node) {
        node.assertType("factor");
        var memberNodeList = node.list("memberList", "memberAccess");

        var objectNode = node.get("object");
        var currentExpression = parseObject(objectNode);
        for (var memberNode : memberNodeList) {
            currentExpression = parseMember(currentExpression, memberNode);
        }

        return currentExpression;
    }

    private static HIRExpression parseMember(HIRExpression previous, Node node) {
        node.assertType("memberAccess");
        if (node.has("id")) {
            var id = new HIRIdentifier(node.getID());
            return new HIRDotNotation(previous, node.getID());
        } else if (node.has("callAccess")) {
            var callAccess = node.get("callAccess");
            var list = ExpressionCommaList.toList(callAccess);
            return new HIRCallNotation(previous, list);
        } else if (node.has("arrayAccess")) {
            var arrayAccess = node.get("arrayAccess");
            var index = HIRExpressionParsing.parse(arrayAccess.get("expression"));
            return new HIRArrayAccessNotation(previous, index);
        }
        throw new NullPointerException("Unknown member");
    }

    private static HIRExpression parseObject(Node node) {
        node.assertType("object");
        if (node.has("blockExpr")) {
            var list = node.get("blockExpr").list("blockExprList", "expression").stream()
                    .map(HIRExpressionParsing::parse).toList();
            return new HIRBlock(list);
        } else if (node.has("number")) {
            var nodeNumber = node.getNumber();
            return new HIRNumber(new BigDecimal(nodeNumber));
        } else if (node.has("string")) {
            var nodeNumber = node.getString();
            var literal = nodeNumber.substring(1, nodeNumber.length() - 1);
            return new HIRString(literal);
        } else if (node.has("id")) {
            var id = node.getID();
            return new HIRIdentifier(id);
        } else if (node.has("expression")) {
            return HIRExpressionParsing.parse(node.get("expression"));
        } else if (node.has("initialisation")) {
            return parseConstruct(node.get("initialisation"));
        }
        throw new NullPointerException("Unknown Object " + node);
    }

    private static HIRConstruct parseConstruct(Node node) {
        node.assertType("initialisation");
        var type = HIRTypeParsing.parse(node.get("type"));
        var list = node.list("initialisationFieldList", "initialisationFieldListOpt",
                "initialisationField").stream().map(ref -> {
            var hirExpression = HIRExpressionParsing.parse(ref.get("expression"));
            return new HIRConstruct.ConstructParam("expr", hirExpression);
        }).toList();
        return new HIRConstruct(type, list);
    }

    private static HIRVarDefinition parseVarDefinition(Node node) {
        node.assertType("variableDefinition");
        var name = node.getID();
        var type = HIRTypeParsing.parse(node.get("type"));
        var values = HIRExpressionParsing.parse(node.get("expression"));
        return new HIRVarDefinition(name, type, values);
    }
}
