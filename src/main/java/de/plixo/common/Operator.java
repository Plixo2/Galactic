package de.plixo.common;

import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.lexer.Node;
import de.plixo.typesys.TypeQuery;
import de.plixo.typesys.types.Primitive;
import de.plixo.typesys.types.Type;
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
    Logic_Negate("!"),
    Negate("-")


        ;

    @Getter
    private final String symbol;

    public static Operator create(Node node) {
        var opString = Objects.requireNonNull(node.child().record()).data();
        for (Operator value : Operator.values()) {
            if(value.symbol.equals(opString)) {
                return value;
            }
        }
        throw new LanguageError(node.region(), "Cant find operator " + node);
    }

    //could use a fallthrough, but hell na
    public Type checkAndGetType(Type left, Type right) {
        return switch (this) {
            case Negate, MULTIPLY,DIVIDE,  ADD, SUBTRACT, GREATER, GREATER_EQUALS, SMALLER,
                    SMALLER_EQUALS -> {
                var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                typeQuery.assertEquality();
                typeQuery = new TypeQuery(right, Primitive.FLOAT);
                typeQuery.assertEquality();
                yield Primitive.FLOAT;
            }
            case EQUALS, NOT_EQUALS -> {
                if (new TypeQuery(left, Primitive.FLOAT).test()) {
                    var typeQuery = new TypeQuery(left, Primitive.FLOAT);
                    typeQuery.assertEquality();
                    typeQuery = new TypeQuery(right, Primitive.FLOAT);
                    typeQuery.assertEquality();
                    yield Primitive.FLOAT;
                } else {
                    var typeQuery = new TypeQuery(left, Primitive.BOOL);
                    typeQuery.assertEquality();
                    typeQuery = new TypeQuery(right, Primitive.BOOL);
                    typeQuery.assertEquality();
                    yield Primitive.BOOL;
                }
            }
            case OR, AND, Logic_Negate -> {
                var typeQuery = new TypeQuery(left, Primitive.BOOL);
                typeQuery.assertEquality();
                typeQuery = new TypeQuery(right, Primitive.BOOL);
                typeQuery.assertEquality();
                yield Primitive.BOOL;
            }
        };
    }
}
