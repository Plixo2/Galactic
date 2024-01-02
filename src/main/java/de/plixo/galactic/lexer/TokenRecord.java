package de.plixo.galactic.lexer;


import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.lexer.tokens.Token;

/**
 * Main Result of the Lexing stage
 *
 * @param token    type of token
 * @param literal  data of the token
 * @param position where the token was captured
 */
public record TokenRecord(Token token, String literal, Region position) {

    public RuntimeException createException() {
        var msg = STR."Unexpected Token (\{token}) '\{literal}' at \{position.toString()}";
        return new FlairCheckException(position, FlairKind.TOKEN, msg);
    }

    public RuntimeException createException(String msg) {
        var message = STR."\{msg} at \{position.toString()}";
        return new FlairCheckException(position, FlairKind.TOKEN, message);
    }

    public String errorMessage() {
        return STR."Unexpected Token (\{token}) '\{literal}': \n\{position.toString()} ";
    }

    public boolean ofType(Class<? extends Token> tokenClass) {
        return token.getClass().equals(tokenClass);
    }
}
