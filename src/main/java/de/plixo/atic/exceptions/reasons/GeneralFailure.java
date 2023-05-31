package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Record;
import de.plixo.atic.lexer.Region;

public final class GeneralFailure extends Failure {
    public GeneralFailure(Region region, String message) {
        setRegion(region);
        setMessage(message);
    }
}
