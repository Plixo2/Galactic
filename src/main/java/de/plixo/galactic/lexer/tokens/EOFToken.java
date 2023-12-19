package de.plixo.galactic.lexer.tokens;

public final class EOFToken extends Token {
    public EOFToken() {
        super("EOF");
    }

    @Override
    public boolean startsWith(char str) {
        return false;
    }

    @Override
    public int matches(String str, int index) {
        return 0;
    }
}
