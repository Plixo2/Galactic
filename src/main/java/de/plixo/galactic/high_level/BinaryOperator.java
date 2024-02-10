package de.plixo.galactic.high_level;

public enum BinaryOperator {
    ADD,
    SUB,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    EQUAL,
    NOT_EQUAL,
    MUL,
    DIV,
    MOD,
    AND,
    OR,
    BIT_AND,
    BIT_OR,
    BIT_XOR,
    ;

    public String toFunctionName() {
        return switch (this) {
            case ADD -> "add";
            case SUB -> "subtract";
            case LESS -> "less";
            case LESS_EQUAL -> "lessEquals";
            case GREATER -> "greater";
            case GREATER_EQUAL -> "greaterEquals";
            case EQUAL -> "equals";
            case NOT_EQUAL -> "notEquals";
            case MUL -> "multiply";
            case DIV -> "divide";
            case MOD -> "mod";
            case AND -> "and";
            case OR -> "or";
            case BIT_AND -> "bitAnd";
            case BIT_OR -> "bitOr";
            case BIT_XOR -> "bitXor";
        };
    }
}
