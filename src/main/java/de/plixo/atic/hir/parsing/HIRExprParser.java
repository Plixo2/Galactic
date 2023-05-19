package de.plixo.atic.hir.parsing;

import de.plixo.atic.common.Constant;
import de.plixo.atic.common.Operator;
import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.hir.expr.*;
import de.plixo.atic.hir.parsing.records.Definition;
import de.plixo.atic.hir.parsing.records.VarDefinition;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;

import java.util.List;

public class HIRExprParser {
    public static HIRExpr parse(Node node) {
        if (node.has("varDefinition")) {
            return parseDefinition(node.get("varDefinition"));
        } else if (node.has("boolArithmetic")) {
            return parseBoolArithmetic(node.get("boolArithmetic"));
        } else if (node.has("returnStatement")) {
            return parseReturn(node.get("returnStatement"));
        } else {
            throw new NullPointerException("cant parse Expression node");
        }
    }

    private static HIRReturn parseReturn(Node node) {
        var expressionOpt = node.get("expressionOpt");
        HIRExpr expr = null;
        if (expressionOpt.has("expression")) {
            expr = HIRExprParser.parse(expressionOpt.get("expression"));
        }
        return new HIRReturn(expr);
    }

    private static HIRDefinition parseDefinition(Node node) {
        return new HIRDefinition(VarDefinition.create(node));
    }

    private static HIRExpr parseBoolArithmetic(Node node) {
        return binaryExpr(node, "comparisonArithmetic", "boolRight", "boolArithmeticFunc",
                "boolArithmetic", HIRExprParser::parseComparisonArithmetic);
    }

    private static HIRExpr parseComparisonArithmetic(Node node) {
        return binaryExpr(node, "arithmetic", "comparisonRight", "comparisonArithmeticFunc",
                "comparisonArithmetic", HIRExprParser::parseArithmetic);
    }

    private static HIRExpr parseArithmetic(Node node) {
        return binaryExpr(node, "term", "arithmeticRight", "arithmeticFunc", "arithmetic",
                HIRExprParser::parseTerm);
    }

    private static HIRExpr parseTerm(Node node) {
        return binaryExpr(node, "factor", "termRight", "termFunc", "term",
                HIRExprParser::parseFactor);
    }

    private static HIRExpr parseFactor(Node node) {
        var obj = node.get("object");
        var currentObject = parseObject(obj);
        for (var access : node.list("memberList", "memberAccess")) {
            currentObject = parseMember(currentObject, access);
        }
        var assignOpt = node.get("assignOpt");
        if (assignOpt.has("expression")) {
            var expression = assignOpt.get("expression");
            var parse = HIRExprParser.parse(expression);
            return new HIRAssign(currentObject, parse);
        } else {
            return currentObject;
        }
    }

    private static HIRExpr parseMember(HIRExpr left, Node node) {
        if (node.has("id")) {
            var id = node.getID();
            return new HIRField(left, id);
        } else if (node.has("expressionList")) {
            var list = node.list("expressionList", "expressionListOpt", "expression").stream()
                    .map(HIRExprParser::parse).toList();
            return new HIRInvoked(left, list);
        } else if (node.has("expression")) {
            //move the syntactic sugar outside
            var index = HIRExprParser.parse(node.get("expression"));
            var indexedFunction = new HIRField(left, "index");
            return new HIRInvoked(indexedFunction, List.of(index));
        } else {
            throw new LanguageError(node.region(), "cant parse Node " + node);
        }
    }

    private static HIRExpr parseObject(Node node) {
        if (node.has("number")) {
            var nodeNumber = node.getNumber();
            Number number;
            if (nodeNumber.contains(".")) {
                number = Double.parseDouble(nodeNumber);
            } else {
                number = Integer.parseInt(nodeNumber);
            }
            return new HIRConstant(new Constant.NumberConstant(number));
        } else if (node.has("id")) {
            return new HIRIdentifier(node.getID());
        } else if (node.has("expression")) {
            return HIRExprParser.parse(node.get("expression"));
        } else if (node.has("blockExpr")) {
            return parseBlock(node.get("blockExpr"));
        } else if (node.has("branchExpr")) {
            return parseBranch(node.get("branchExpr"));
        } else if (node.has("function")) {
            return parseFunction(node.get("function"));
        } else if (node.has("unary")) {
            return parseUnary(node.get("unary"));
        } else {
            throw new LanguageError(node.region(), "cant parse Node " + node);
        }
    }

    private static HIRUnary parseUnary(Node node) {
        var factor = node.get("factor");
        if (node.children().size() == 2) {
            return new HIRUnary(parseFactor(factor), Operator.SUBTRACT);
        } else {
            return new HIRUnary(parseFactor(factor), Operator.LOGIC_NEGATE);
        }
    }

    private static HIRBlock parseBlock(Node node) {
        var list = node.list("blockExprList", "expression").stream().map(HIRExprParser::parse)
                .toList();
        return new HIRBlock(list);
    }

    private static HIRBranch parseBranch(Node node) {
        var condition = HIRExprParser.parse(node.get("expression"));
        var body = HIRExprParser.parse(node.get("body").get("expression"));
        HIRExpr elseExpr = null;
        var branchOpt = node.get("branchOpt");
        if (branchOpt.has("elseOpt")) {
            var elseExprNode = branchOpt.get("elseOpt").get("body").get("expression");
            elseExpr = HIRExprParser.parse(elseExprNode);
        } else if (branchOpt.has("elseifOpt")) {
            var elseifOpt = branchOpt.get("elseifOpt");
            elseExpr = HIRExprParser.parseBranch(elseifOpt);
        }
        return new HIRBranch(condition, body, elseExpr);
    }

    private static HIRFunction parseFunction(Node node) {
        var args = node.list("argumentList", "argumentListOpt", "definition").stream()
                .map(Definition::create)
                .map(ref -> new ArgDefinition(ref.name(), ref.typehint(), ref.expression()))
                .toList();
        var returnTypeOpt = node.get("returnTypeOpt");
        var ownerDef = node.get("ownerDef");
        HIRType returnType = null;
        if (returnTypeOpt.has("type")) {
            returnType = HIRTypeParser.parse(returnTypeOpt.get("type"));
        }
        var body = HIRExprParser.parse(node.get("expression"));
        HIRType owner = null;
        if (ownerDef.has("type")) {
            owner = HIRTypeParser.parse(ownerDef.get("type"));
        }

        return new HIRFunction(args, returnType, owner, body);
    }

    private static HIRExpr binaryExpr(Node node, String next, String right, String function,
                                      String current, HIRExprProvider nextFunction) {
        var leftNode = nextFunction.get(node.get(next));
        var rightSide = node.get(right);
        if (rightSide.has(function)) {
            var operators = rightSide.get(function);
            var operator = Operator.create(operators);
            var rightNode = binaryExpr(rightSide.get(current), next, right, function, current,
                    nextFunction);
            return new HIRBinOp(leftNode, rightNode, operator);
        } else {
            return leftNode;
        }
    }

    private interface HIRExprProvider {
        HIRExpr get(Node node);
    }
}
