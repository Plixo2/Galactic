package de.plixo.common;

import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.lexer.Node;
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
}
