package de.plixo.atic.lexer;


public record Position(int line, int from, int to) {

    @Override
    public String toString() {
        return line + ":(" + from + "-" + to + ")";
    }
}
