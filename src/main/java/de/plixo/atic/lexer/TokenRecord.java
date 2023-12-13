package de.plixo.atic.lexer;


import de.plixo.atic.lexer.tokens.Token;

/**
 * Main Result of the Lexing stage
 * @param token type of token
 * @param literal data of the token
 * @param position where the token was captured
 */
public record TokenRecord(Token token, String literal, Position position) {

    public RuntimeException createException() {
        return new RuntimeException("Unexpected Token (" + token + ") '" + literal + "' at " + position.toString());
    }

    public RuntimeException createException(String msg) {
        return new RuntimeException(msg + " at " + position.toString());
    }

    public boolean ofType(Class<? extends Token> tokenClass) {
        return token.getClass().equals(tokenClass);
    }
}
