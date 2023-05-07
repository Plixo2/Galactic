package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Record;

public class FileIOError extends CompileError {
    public FileIOError(String msg, Throwable e) {
        super(msg, e);
    }

    public FileIOError(String message) {
        super(message);
    }
}
