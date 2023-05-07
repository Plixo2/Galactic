package de.plixo.atic.exceptions;

public class CompileError extends RuntimeException {

    public CompileError() {
    }

    public CompileError(String message) {
        super(message);
    }

    public CompileError(String message, Throwable cause) {
        super(message, cause);
    }
}
