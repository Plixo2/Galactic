package de.plixo.galactic.lexer.tokens;

/**
 * CommentToken for every after //
 */
public final class CommentToken extends Token {

    public CommentToken() {
        super("");
    }

    @Override
    public boolean startsWith(char str) {
        return str == '/';
    }

    @Override
    public int matches(String str, int index) {
        if (index + 1 >= str.length()) return -1;
        if (str.charAt(index + 1) != '/') return -1;
        return str.length();
    }
}
