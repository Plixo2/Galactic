package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Region;

public class FailedTokenCaptureError extends LanguageError {

    public FailedTokenCaptureError(Region region, String begin) {
        super(region, "Unexpected token \"" + begin.substring(0, Math.min(begin.length(), 10)) + " \"");
    }
}
