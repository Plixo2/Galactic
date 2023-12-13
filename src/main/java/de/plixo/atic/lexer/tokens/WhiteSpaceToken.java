package de.plixo.atic.lexer.tokens;

/**
 * Token for all types of whitespace
 */
public final class WhiteSpaceToken extends Token {
    public WhiteSpaceToken() {
        super("");
    }

    @Override
    public boolean startsWith(char str) {
        return Character.isWhitespace(str);
    }

    @Override
    public int matches(String str, int index) {
        int i;
        for (i = index; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                break;
            }
        }
        return i;
    }
}
