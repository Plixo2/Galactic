package de.plixo.galactic.high_level;

import de.plixo.galactic.high_level.expressions.HIRBinary;
import de.plixo.galactic.high_level.expressions.HIRExpression;
import de.plixo.galactic.high_level.expressions.HIRUnary;
import de.plixo.galactic.parsing.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*

ConditionalOrExpression := ConditionalAndExpression ConditionalOrExpressionRight!
ConditionalOrExpressionRight := ConditionalOrExpressionFunction ConditionalOrExpression ||
ConditionalOrExpressionFunction := "||"

ConditionalAndExpression := InclusiveOrExpression ConditionalAndExpressionRight!
ConditionalAndExpressionRight := ConditionalAndExpressionFunction ConditionalAndExpression ||
ConditionalAndExpressionFunction := "&&"

InclusiveOrExpression := ExclusiveOrExpression InclusiveOrExpressionRight!
InclusiveOrExpressionRight := InclusiveOrExpressionFunction InclusiveOrExpression ||
InclusiveOrExpressionFunction := "|"

ExclusiveOrExpression := AndExpression ExclusiveOrExpressionRight!
ExclusiveOrExpressionRight := ExclusiveOrExpressionFunction ExclusiveOrExpression ||
ExclusiveOrExpressionFunction := "^"

AndExpression := EqualityExpression AndExpressionRight!
AndExpressionRight := AndExpressionFunction AndExpression ||
AndExpressionFunction := "&"

EqualityExpression := RelationalExpression EqualityExpressionRight!
EqualityExpressionRight := EqualityExpressionFunction EqualityExpression ||
EqualityExpressionFunction := "==" | "!="

RelationalExpression := AdditiveExpression RelationalExpressionRight!
RelationalExpressionRight := RelationalExpressionFunction RelationalExpression ||
RelationalExpressionFunction := ">=" | "<=" | ">" | "<"

AdditiveExpression := MultiplicativeExpression AdditiveExpressionRight!
AdditiveExpressionRight := AdditiveExpressionFunction AdditiveExpression ||
AdditiveExpressionFunction := "+" | "-"

MultiplicativeExpression := UnaryExpression MultiplicativeExpressionRight!
MultiplicativeExpressionRight := MultiplicativeExpressionFunction MultiplicativeExpression ||
MultiplicativeExpressionFunction := "*" | "/" | "%"
 */

public class HIRMathParsing {
    private static Map<String, BinNodeFormat> lookup;

    static {
        var lookup = new HashMap<String, BinNodeFormat>();
        lookup.put("conditionalorexpression", new BinNodeFormat("ConditionalAndExpression", "ConditionalOrExpressionRight",
                "ConditionalOrExpressionFunction"));
        lookup.put("ConditionalAndExpression", new BinNodeFormat("InclusiveOrExpression", "ConditionalAndExpressionRight", "ConditionalAndExpressionFunction"));
        lookup.put("InclusiveOrExpression", new BinNodeFormat("ExclusiveOrExpression", "InclusiveOrExpressionRight", "InclusiveOrExpressionFunction"));
        lookup.put("ExclusiveOrExpression", new BinNodeFormat("AndExpression", "ExclusiveOrExpressionRight", "ExclusiveOrExpressionFunction"));
        lookup.put("AndExpression", new BinNodeFormat("EqualityExpression", "AndExpressionRight", "AndExpressionFunction"));
        lookup.put("EqualityExpression", new BinNodeFormat("RelationalExpression", "EqualityExpressionRight", "EqualityExpressionFunction"));
        lookup.put("RelationalExpression", new BinNodeFormat("AdditiveExpression", "RelationalExpressionRight", "RelationalExpressionFunction"));
        lookup.put("AdditiveExpression", new BinNodeFormat("MultiplicativeExpression", "AdditiveExpressionRight", "AdditiveExpressionFunction"));
        lookup.put("MultiplicativeExpression", new BinNodeFormat("UnaryExpression", "MultiplicativeExpressionRight", "MultiplicativeExpressionFunction"));

        HIRMathParsing.lookup = new HashMap<>();

        lookup.forEach((key, value) -> HIRMathParsing.lookup.put(key.toLowerCase(), value));
    }

    public static HIRExpression parse(Node node) {
        return genericBinary(node);
//        node.assertType("ConditionalOrExpression");
//        var next = node.get("ConditionalAndExpression");
//        var left = parseConditionalAndExpression(next);
//        var rightPart = node.get("ConditionalOrExpressionRight");
//        if (rightPart.has("ConditionalOrExpressionFunction")) {
//            var operator = rightPart.get("ConditionalOrExpressionFunction");
//            var rightNode = rightPart.get("ConditionalOrExpression");
//            var right = parse(rightNode);
//            return new HIRBinary(node.region(), left, getOperator(operator), right);
//        } else {
//            return left;
//        }
    }

    private static HIRExpression genericBinary(Node node) {
        var name = node.name();
        var entry = Objects.requireNonNull(lookup.get(name.toLowerCase()), STR."No entry for \{name}");
        HIRExpression left;
        if (entry.next.equals("UnaryExpression")) {
            left = parseUnaryExpression(node.get("UnaryExpression"));
        } else {
            var next = node.get(entry.next);
            left = genericBinary(next);
        }
        var rightPart = node.get(entry.rightSide);
        if (rightPart.has(entry.function)) {
            var operator = rightPart.get(entry.function);
            var rightNode = rightPart.get(name);
            var right = genericBinary(rightNode);
            return new HIRBinary(node.region(), left, getBinOperator(operator), right);
        } else {
            return left;
        }
    }

    private static HIRExpression parseUnaryExpression(Node node) {
        var factor = HIRExpressionParsing.parseFactor(node.get("factor"));
        if (node.has("UnaryExpressionFunction")) {
            var operator = node.get("UnaryExpressionFunction");
            var literal = operator.child().record().literal();
            UnaryOperator not;
            switch (literal) {
                case "!" -> not = UnaryOperator.NOT;
                case "-" -> not = UnaryOperator.NEGATE;
                case "~" -> not = UnaryOperator.BIT_COMPLEMENT;
                case "+" -> {
                    return factor;
                }
                default -> throw new IllegalStateException(STR."Unexpected value: \{literal}");
            }
            return new HIRUnary(node.region(), factor, not);
        }
        return factor;
    }
    private static BinaryOperator getBinOperator(Node node) {
        var literal = node.child().record().literal();
        return switch (literal) {
            case "||" -> BinaryOperator.OR;
            case "&&" -> BinaryOperator.AND;
            case "|" -> BinaryOperator.BIT_OR;
            case "^" -> BinaryOperator.BIT_XOR;
            case "&" -> BinaryOperator.BIT_AND;
            case "==" -> BinaryOperator.EQUAL;
            case "!=" -> BinaryOperator.NOT_EQUAL;
            case ">" -> BinaryOperator.GREATER;
            case "<" -> BinaryOperator.LESS;
            case ">=" -> BinaryOperator.GREATER_EQUAL;
            case "<=" -> BinaryOperator.LESS_EQUAL;
            case "+" -> BinaryOperator.ADD;
            case "-" -> BinaryOperator.SUB;
            case "*" -> BinaryOperator.MUL;
            case "/" -> BinaryOperator.DIV;
            case "%" -> BinaryOperator.MOD;
            default -> throw new IllegalStateException(STR."Unexpected value: \{literal}");
        };
    }


    private record BinNodeFormat(String next, String rightSide, String function) {

    }
}
