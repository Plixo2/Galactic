package de.plixo.galactic.exception;

/**
 * Base exception thrown by the compiler.
 */
public class FlairException extends RuntimeException {

    public FlairException() {
    }

    public FlairException(String message) {
        super(message);
    }

    public FlairException(String message, Throwable cause) {
        super(message, cause);
    }
}
