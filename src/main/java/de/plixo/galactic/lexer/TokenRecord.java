package de.plixo.galactic.lexer;


import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.lexer.tokens.Token;

/**
 * Main Result of the Lexing stage
 *
 * @param token    type of token
 * @param literal  data of the token
 * @param position where the token was captured
 */
public record TokenRecord(Token token, String literal, Position position) {

    public RuntimeException createException() {
        return new FlairException(
                "Unexpected Token (" + token + ") '" + literal + "' at " + position.toString());
    }

    public RuntimeException createException(String msg) {
        return new FlairException(msg + " at " + position.toString());
    }

    public String errorMessage() {
        return "Unexpected Token (" + token + ") '" + literal + "': \n" + position.toString() + " ";
    }

    public boolean ofType(Class<? extends Token> tokenClass) {
        return token.getClass().equals(tokenClass);
    }
}
