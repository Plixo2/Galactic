package de.plixo.atic.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
