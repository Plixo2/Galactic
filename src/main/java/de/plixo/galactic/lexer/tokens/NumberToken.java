package de.plixo.galactic.lexer.tokens;

/**
 * Token for everything starting with a number
 */
public final class NumberToken extends Token {
    public NumberToken() {
        super("number");
    }

    @Override
    public boolean startsWith(char str) {
        return isNumber(str);
    }

    @Override
    public int matches(String str, int index) {
        int i;
        var length = str.length();
        for (i = index; i < length; i++) {
            if (!isNumberSuffix(str.charAt(i))) {
                break;
            }
        }
        if (i < length) {
            if (isInAlphabet(str.charAt(i))) {
                i += 1;
            }
        }

        return i;
    }

    private boolean isNumberSuffix(char point) {
        return isNumber(point) || point == '.';
    }

    private boolean isNumber(char point) {
        return (point >= '0' && point <= '9');
    }

    private boolean isInAlphabet(char point) {
        return (point >= 'A' && point <= 'Z') || (point >= 'a' && point <= 'z');
    }
}
