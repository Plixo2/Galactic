package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Region;

import java.io.File;

public class LanguageError extends CompileError {
    public static File errorFile;

    public LanguageError(Region region) {
        super("Error at " + region);
    }

    public LanguageError(Region region, String message) {
        super(message + " at " + createMsg(region));
    }

    private static String createMsg(Region region) {
        if (errorFile == null) {
            return region.toString();
        } else {
            var path = errorFile.getAbsolutePath();
            return path + ":" + (region.left().line()) + ":" + (region.left().from());
        }
    }

    public LanguageError(Region region, String message, Throwable cause) {
        super(message + " at " + region, cause);
    }
}
