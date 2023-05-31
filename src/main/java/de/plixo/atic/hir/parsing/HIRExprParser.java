package de.plixo.atic.hir.parsing;

import de.plixo.atic.common.Constant;
import de.plixo.atic.common.Operator;
import de.plixo.atic.exceptions.reasons.GrammarNodeFailure;
import de.plixo.atic.hir.expr.*;
import de.plixo.atic.hir.parsing.records.Definition;
import de.plixo.atic.hir.parsing.records.VarDefinition;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;

import java.util.ArrayList;
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
            throw new GrammarNodeFailure("cant parse Expression node", node).create();
        }
    }

    private static HIRReturn parseReturn(Node node) {
        var expressionOpt = node.get("expressionOpt");
        HIRExpr expr = null;
        if (expressionOpt.has("expression")) {
            expr = HIRExprParser.parse(expressionOpt.get("expression"));
        }
        return new HIRReturn(node.region(), expr);
    }

    private static HIRDefinition parseDefinition(Node node) {
        return new HIRDefinition(node.region(), VarDefinition.create(node));
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
            return new HIRAssign(node.region(), currentObject, parse);
        } else {
            return currentObject;
        }
    }

    private static HIRExpr parseMember(HIRExpr left, Node node) {
        if (node.has("id")) {
            var id = node.getID();
            return new HIRField(node.region(), left, id);
        } else if (node.has("callAccess")) {
            var callAccess = node.get("callAccess");
            List<HIRExpr> list;
            if (callAccess.has("expressionList")) {
                list = callAccess.list("expressionList", "expressionListOpt", "expression")
                        .stream().map(HIRExprParser::parse).toList();
            } else {
                list = List.of(HIRExprParser.parse(callAccess.get("expression")));
            }
            return new HIRInvoked(node.region(), left, list);
        } else if (node.has("expression")) {
            //move the syntactic sugar outside
            var index = HIRExprParser.parse(node.get("expression"));
            var indexedFunction = new HIRField(node.region(), left, "index");
            return new HIRInvoked(node.region(), indexedFunction, List.of(index));
        } else {
            throw new GrammarNodeFailure("cant parse Node", node).create();
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
            return new HIRConstant(node.region(), new Constant.NumberConstant(number));
        } else if (node.has("string")) {
            var nodeNumber = node.getString();
            var literal = nodeNumber.substring(1, nodeNumber.length() - 1);
            return new HIRConstant(node.region(), new Constant.StringConstant(literal));
        } else if (node.has("id")) {
            return new HIRIdentifier(node.region(), node.getID());
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
            throw new GrammarNodeFailure("cant parse Node", node).create();
        }
    }

    private static HIRUnary parseUnary(Node node) {
        var factor = node.get("factor");
        if (node.children().size() == 2) {
            return new HIRUnary(node.region(), parseFactor(factor), Operator.SUBTRACT);
        } else {
            return new HIRUnary(node.region(), parseFactor(factor), Operator.LOGIC_NEGATE);
        }
    }

    private static HIRBlock parseBlock(Node node) {
        var list = node.list("blockExprList", "expression").stream().map(HIRExprParser::parse)
                .toList();
        return new HIRBlock(node.region(), list);
    }

    private static HIRBranch parseBranch(Node node) {
        var condition = HIRExprParser.parse(node.get("expression"));
        var bdy = node.get("body");
        HIRExpr body;
        if (bdy.has("expression")) {
            body = HIRExprParser.parse(bdy.get("expression"));
        } else {
            body = HIRExprParser.parseBlock(bdy.get("blockExpr"));
        }
        HIRExpr elseExpr = null;
        var branchOpt = node.get("branchOpt");
        if (branchOpt.has("elseOpt")) {
            var elseBody = branchOpt.get("elseOpt").get("body");
            HIRExpr elseExprNode;
            if (elseBody.has("expression")) {
                elseExpr = HIRExprParser.parse(elseBody.get("expression"));
            } else {
                elseExpr = HIRExprParser.parseBlock(elseBody.get("blockExpr"));
            }
        } else if (branchOpt.has("elseifOpt")) {
            var elseifOpt = branchOpt.get("elseifOpt");
            elseExpr = HIRExprParser.parseBranch(elseifOpt);
        }
        return new HIRBranch(node.region(), condition, body, elseExpr);
    }

    private static HIRFunction parseFunction(Node node) {
        var args = node.list("argumentList", "argumentListOpt", "definition").stream()
                .map(Definition::create)
                .map(ref -> new ArgDefinition(ref.region(), ref.name(), ref.typehint(),
                        ref.expression(), new ArrayList<>())).toList();
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

        return new HIRFunction(node.region(), args, returnType, owner, body);
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
            return new HIRBinOp(node.region(), leftNode, rightNode, operator);
        } else {
            return leftNode;
        }
    }

    private interface HIRExprProvider {
        HIRExpr get(Node node);
    }
}
