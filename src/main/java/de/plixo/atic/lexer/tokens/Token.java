package de.plixo.atic.lexer.tokens;

import lombok.Getter;

/**
 * Superclass for all tokens
 */
@Getter
public sealed abstract class Token
        permits CharToken, CommentToken, EOFToken, LiteralToken, NumberToken, StringToken,
        UnknownToken, WhiteSpaceToken, WordToken {

    /**
     * Alias used inside the grammar
     */
    private final String alias;

    public Token(String alias) {
        this.alias = alias;
    }

    public abstract boolean startsWith(char str);

    /**
     * Returns -1 if the token does not match, returns the next
     * index if it does.
     */
    public abstract int matches(String str, int index);

    public static Token unknownToken() {
        return new UnknownToken();
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + alias + "}";
    }
}
