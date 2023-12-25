package de.plixo.galactic.exception;

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
