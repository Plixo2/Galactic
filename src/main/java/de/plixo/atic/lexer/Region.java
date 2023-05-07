package de.plixo.atic.lexer;

public record Region(Position left, Position right) {

    public static Region fromPosition(Position position) {
        return new Region(position, position);
    }

    @Override
    public String toString() {
        return "(" + left.line() + ":" + left.from() + ")-(" + right.line() + ":" + right.to() + ")";
    }
}
