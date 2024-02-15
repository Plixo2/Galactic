package de.plixo.galactic.lexer;

/**
 * Region over a node or snippet
 *
 * @param left  upper location
 * @param right lower location
 */
public record Region(Position left, Position right) {

    public int minLine() {
        return left.line();
    }

    public int maxLine() {
        return right.line();
    }

    public Position minPosition() {
        if (left.line() < right.line()) {
            return left;
        } else if (left.line() > right.line()) {
            return right;
        } else {
            return left.character() < right.character() ? left : right;
        }
    }

    public Position maxPosition() {
        if (left.line() < right.line()) {
            return right;
        } else if (left.line() > right.line()) {
            return left;
        } else {
            return left.character() < right.character() ? right : left;
        }
    }

    public String dotFormat() {
        return STR."\{left.line()}:\{left.character()} -> \{right.line()}:\{right.character()}";
    }
}
