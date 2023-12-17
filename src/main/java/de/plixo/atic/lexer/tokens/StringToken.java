package de.plixo.atic.lexer.tokens;

/**
 * Token for a standard escapable string with '\'
 */
public final class StringToken extends Token {
    public StringToken() {
        super("string");
    }

    @Override
    public boolean startsWith(char str) {
        return str == '"';
    }

    @Override
    public int matches(String str, int index) {

        boolean waitForChar = false;
        for (int i = index + 1; i < str.length(); i++) {
            var c = str.charAt(i);
            if (waitForChar) {
                waitForChar = false;
                continue;
            }
            if (c == '\\') {
                waitForChar = true;
            }
            if (c == '"') {
                return i + 1;
            }
        }

        return -1;
    }

}
