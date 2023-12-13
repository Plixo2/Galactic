package de.plixo.atic.lexer.tokens;

/**
 * Token for unknown characters
 */
public final class UnknownToken extends Token {

    UnknownToken() {
        super("?????");
    }
    @Override
    public boolean startsWith(char str) {
        return false;
    }

    @Override
    public int matches(String str, int index) {
        return -1;
    }


}
