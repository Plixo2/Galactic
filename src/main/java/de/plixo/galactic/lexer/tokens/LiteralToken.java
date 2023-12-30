package de.plixo.galactic.lexer.tokens;

/**
 * Token that matches a String.
 * It is used for matching keywords and other literals.
 */
public final class LiteralToken extends Token {
    private final String literal;

    public LiteralToken(String literal) {
        super(literal);
        this.literal = literal;
    }

    @Override
    public boolean startsWith(char str) {
        if (literal.isEmpty()) {
            return false;
        }
        return literal.charAt(0) == str;
    }

    @Override
    public int matches(String str, int index) {
        if (index + literal.length() > str.length()) {
            return -1;
        }
        for (int i = 0; i < literal.length(); i++) {
            var stringIndex = index + i;
            var literalChar = literal.charAt(i);
            var strChar = str.charAt(stringIndex);
            if (literalChar != strChar) {
                return -1;
            }
        }
        if (index + literal.length() + 1 < str.length()) {
            var next = str.charAt(index + literal.length());
            if (Character.isLetterOrDigit(next)) {
                return -1;
            }
        }

        return index + literal.length();
    }

}
