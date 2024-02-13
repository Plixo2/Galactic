package de.plixo.galactic.high_level;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.high_level.expressions.*;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.high_level.utils.ExpressionCommaList;
import de.plixo.galactic.parsing.Node;

import java.util.ArrayList;
import java.util.List;


/**
 * Parses an expression node from the CFG into a HIRExpression
 */


public class HIRExpressionParsing {
    public static HIRExpression parse(Node node) {
        node.assertType("expression");
        if (node.has("ConditionalOrExpression")) {
            return HIRMathParsing.parse(node.get("ConditionalOrExpression"));
        } else if (node.has("variableDefinition")) {
            return parseVarDefinition(node.get("variableDefinition"));
        } else if(node.has("blockExpr")) {
            return parseBlock(node.get("blockExpr"));
        } else if(node.has("expression")) {
            return parse(node.get("expression"));
        } else if (node.has("macro")) {
            return parseMacro(node.get("macro"));
        }

        throw new NullPointerException(
                STR."unknown expression \{node.children().stream().map(Node::name)
                        .toList()} of \n \{node}");
    }

    private static HIRExpression parseMacro(Node node) {
        node.assertType("macro");
        if (node.has("ConditionalOrExpression")) {
            return HIRMathParsing.parse(node.get("ConditionalOrExpression"));
        } else if (node.has("variableDefinition")) {
            return parseVarDefinition(node.get("variableDefinition"));
        } else if(node.has("blockExpr")) {
            return parseBlock(node.get("blockExpr"));
        } else if(node.has("expression")) {
            return parse(node.get("expression"));
        } else if (node.has("macro")) {
            return parseMacro(node.get("macro"));
        }
        throw new NullPointerException(
                STR."unknown expression \{node.children().stream().map(Node::name)
                        .toList()} of \n \{node}");
    }

    private static HIRBranch parseBranch(Node node) {
        node.assertType("branch");
        var expression = HIRExpressionParsing.parse(node.get("expression"));
        var body = node.get("body");
        var bodyExpression = HIRExpressionParsing.parseBlock(body.get("blockExpr"));
        var branchOpt = node.get("branchOpt");
        HIRExpression elsePart = null;
        if (branchOpt.has("expression")) {
            elsePart = HIRExpressionParsing.parse(branchOpt.get("expression"));
        }

        return new HIRBranch(node.region(), expression, bodyExpression, elsePart);
    }

    public static HIRExpression parseFactor(Node node) {
        node.assertType("factor");
        var memberNodeList = node.list("postfixList", "postFix");

        var objectNode = node.get("object");
        var currentExpression = parseObject(objectNode);
        for (var memberNode : memberNodeList) {
            currentExpression = parseMember(currentExpression, memberNode);
        }

        var assignOpt = node.get("assignOpt");
        if (assignOpt.has("expression")) {
            var expression = assignOpt.get("expression");
            var value = HIRExpressionParsing.parse(expression);
            return new HIRAssign(node.region(), currentExpression, value);
        }

        return currentExpression;
    }

    private static HIRExpression parseMember(HIRExpression previous, Node node) {
        node.assertType("postFix");
        if (node.has("id")) {
            return new HIRDotNotation(node.region(), previous, node.getID());
        } else if (node.has("callAccess")) {
            var callAccess = node.get("callAccess");
            List<HIRExpression> list;
            if (callAccess.has("expression")) {
                var expressionNode = callAccess.get("expression");
                var hirExpression = HIRExpressionParsing.parse(expressionNode);
                list = new ArrayList<>();
                list.add(hirExpression);
            } else {
                list = ExpressionCommaList.toList(callAccess);
            }
            return new HIRCallNotation(node.region(), previous, list);
        } else if (node.has("cast")) {
            var cast = node.get("cast");
            var type = HIRTypeParsing.parse(cast.get("type"));
            return new HIRCast(node.region(), previous, type);
        } else if (node.has("castCheck")) {
            var cast = node.get("castCheck");
            var type = HIRTypeParsing.parse(cast.get("type"));
            return new HIRCastCheck(node.region(), previous, type);
        }

        throw new NullPointerException("Unknown member");
    }

    private static HIRExpression parseObject(Node node) {
        node.assertType("object");
        if (node.has("blockExpr")) {
            return parseBlock(node.get("blockExpr"));
        } else if (node.has("number")) {
            var nodeNumber = node.getNumber();
            try {
                return new HIRNumber(node.region(), nodeNumber);
            } catch (NumberFormatException e) {
                throw new FlairCheckException(node.region(), FlairKind.FORMAT,
                        STR."Wrong number format \{nodeNumber}");
            }
        } else if (node.has("string")) {
            var nodeNumber = node.getString();
            var literal = nodeNumber.substring(1, nodeNumber.length() - 1);
            return new HIRString(node.region(), literal);
        } else if (node.has("id")) {
            var id = node.getID();
            return new HIRIdentifier(node.region(), id);
        } else if (node.has("expression")) {
            return HIRExpressionParsing.parse(node.get("expression"));
        } else if (node.has("initialisation")) {
            return parseConstruct(node.get("initialisation"));
        } else if (node.has("branch")) {
            return parseBranch(node.get("branch"));
        } else if (node.has("superCall")) {
            return parseSuperCall(node.get("superCall"));
        } else if (node.has("this")) {
            return new HIRThis(node.region());
        } else if (node.has("while")) {
            return parseWhile(node.get("while"));
        }
        throw new NullPointerException(STR."Unknown Object \{node}");
    }

    private static HIRWhile parseWhile(Node node) {
        node.assertType("while");
        var expression = HIRExpressionParsing.parse(node.get("expression"));
        var bodyExpression = HIRExpressionParsing.parseBlock(node.get("blockExpr"));
        return new HIRWhile(node.region(), expression, bodyExpression);
    }

    private static HIRSuperCall parseSuperCall(Node node) {
        node.assertType("superCall");
        HIRType superType = null;

        if (node.has("type")) {
            superType = HIRTypeParsing.parse(node.get("type"));
        }
        String name = null;
        if (node.has("id")) {
            name = node.getID();
        }
        var arguments = ExpressionCommaList.toList(node.get("expressionList"));
        return new HIRSuperCall(node.region(), superType, name, arguments);
    }

    private static HIRConstruct parseConstruct(Node node) {
        node.assertType("initialisation");
        var type = HIRTypeParsing.parse(node.get("type"));
        var initialisationList = node.get("initialisationList");
        var list = initialisationList.list("initialisationFieldList", "initialisationFieldListOpt",
                "initialisationField").stream().map(ref -> {
            var hirExpression = HIRExpressionParsing.parse(ref.get("expression"));
            return new HIRConstruct.ConstructParam(ref.region(), "expr", hirExpression);
        }).toList();
        return new HIRConstruct(node.region(), type, list);
    }

    private static HIRVarDefinition parseVarDefinition(Node node) {
        node.assertType("variableDefinition");
        var name = node.getID();
        var typeHint = node.get("typeHint");
        HIRType type;
        if (typeHint.has("type")) {
            type = HIRTypeParsing.parse(typeHint.get("type"));
        } else {
            type = null;
        }

        var values = HIRExpressionParsing.parse(node.get("expression"));
        return new HIRVarDefinition(node.region(), name, type, values);
    }

    private static HIRExpression parseBlock(Node node) {
//        System.out.println("parsing block of " + node.region().left().file());
        node.assertType("blockExpr");

        var list =
                node.list("blockExprList", "expression").stream().map(HIRExpressionParsing::parse)
                        .toList();
        return new HIRBlock(node.region(), list);
    }
}
