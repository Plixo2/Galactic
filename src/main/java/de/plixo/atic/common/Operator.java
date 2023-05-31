package de.plixo.atic.common;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.lexer.Region;
import de.plixo.atic.typing.TypeQuery;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import de.plixo.atic.exceptions.reasons.FeatureFailure;
import de.plixo.atic.exceptions.reasons.GrammarNodeFailure;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
public enum Operator {
    ADD("+"),
    SUBTRACT("-"),

    AND("&&"),
    OR("||"),

    GREATER(">"),

    GREATER_EQUALS(">="),

    SMALLER("<"),

    SMALLER_EQUALS("<="),

    EQUALS("=="),

    NOT_EQUALS("!="),

    MULTIPLY("/"),

    DIVIDE("*"),
    LOGIC_NEGATE("!");

    @Getter
    private final String symbol;

    public static Operator create(Node node) {
        var opString = Objects.requireNonNull(node.child().record()).data();
        for (Operator value : Operator.values()) {
            if (value.symbol.equals(opString)) {
                return value;
            }
        }
        throw new GrammarNodeFailure("cant find operator " + opString, node).create();
    }

    public Type checkAndGetTypeUnary(Region region, Type left) {
        return switch (this) {
            case SUBTRACT -> {
                var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                typeQuery.assertEquality(region);
                yield Primitive.FLOAT;
            }
            case LOGIC_NEGATE -> {
                var typeQuery = new TypeQuery(left, Primitive.BOOL);
                typeQuery.assertEquality(region);
                yield Primitive.BOOL;
            }
            default -> throw new FeatureFailure("Unary operations for " + this).create();
        };
    }

    public Type checkAndGetTypeBinary(Region region, Type left, Type right) {
        var pullEqual = new TypeQuery(left, right);
        if (pullEqual.test()) {
            pullEqual.mutate();
        }
        return switch (this) {
            case MULTIPLY, DIVIDE, ADD, SUBTRACT -> {
                if (new TypeQuery(left, Primitive.FLOAT).test()) {
                    var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    yield Primitive.FLOAT;
                } else {
                    var typeQuery = new TypeQuery(left, Primitive.INT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.INT);
                    typeQuery.assertEquality(region);
                    yield Primitive.INT;
                }
            }
            case EQUALS, NOT_EQUALS -> {
                if (new TypeQuery(left, Primitive.INT).test()) {
                    var typeQuery = new TypeQuery(left, Primitive.INT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.INT);
                    typeQuery.assertEquality(region);
                    yield Primitive.BOOL;
                } else if (new TypeQuery(left, Primitive.FLOAT).test()) {
                    var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    yield Primitive.BOOL;
                } else {
                    var typeQuery = new TypeQuery(left, Primitive.BOOL);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.BOOL);
                    typeQuery.assertEquality(region);
                    yield Primitive.BOOL;
                }
            }
            case GREATER, GREATER_EQUALS, SMALLER, SMALLER_EQUALS -> {
                if (new TypeQuery(left, Primitive.FLOAT).test()) {
                    var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.FLOAT);
                    typeQuery.assertEquality(region);
                    yield Primitive.BOOL;
                } else {
                    var typeQuery = new TypeQuery(left, Primitive.INT);
                    typeQuery.assertEquality(region);
                    typeQuery = new TypeQuery(right, Primitive.INT);
                    typeQuery.assertEquality(region);
                    yield Primitive.BOOL;
                }
            }
            case OR, AND, LOGIC_NEGATE -> {
                var typeQuery = new TypeQuery(left, Primitive.BOOL);
                typeQuery.assertEquality(region);
                typeQuery = new TypeQuery(right, Primitive.BOOL);
                typeQuery.assertEquality(region);
                yield Primitive.BOOL;
            }
        };
    }
}
