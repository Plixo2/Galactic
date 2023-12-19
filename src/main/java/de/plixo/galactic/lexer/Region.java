package de.plixo.galactic.lexer;

/**
 * Region over a node or snippet
 * @param left upper location
 * @param right lower location
 */
public record Region(Position left, Position right) {

    public int minLine() {
        return left.line();
    }
    public int maxLine() {
        return right.line();
    }
}
