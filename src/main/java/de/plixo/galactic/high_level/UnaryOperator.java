package de.plixo.galactic.high_level;

public enum UnaryOperator {
       NOT, NEGATE,
    BIT_COMPLEMENT;

    public String toFunctionName() {
        return switch (this) {
            case NOT -> "not";
            case NEGATE -> "negate";
            case BIT_COMPLEMENT -> "bitComplement";
        };
    }
}
