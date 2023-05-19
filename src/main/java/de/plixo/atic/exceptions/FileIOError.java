package de.plixo.atic.exceptions;


public class FileIOError extends CompileError {
    public FileIOError(String msg, Throwable e) {
        super(msg, e);
    }

    public FileIOError(String message) {
        super(message);
    }
}
