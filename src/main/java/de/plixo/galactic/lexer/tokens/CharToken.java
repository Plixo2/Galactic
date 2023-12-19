package de.plixo.galactic.lexer.tokens;


import lombok.Getter;

/**
 * Token for a single character
 */
@Getter
public final class CharToken extends Token {
    private final char codePoint;

    public CharToken(char codePoint) {
        super(String.valueOf(codePoint));
        this.codePoint = codePoint;
    }

    @Override
    public boolean startsWith(char str) {
        return str == codePoint;
    }


    /**
     * no check needed, already performed by 'startsWith'
     */
    @Override
    public int matches(String str, int index) {
        return index + 1;
    }
}
