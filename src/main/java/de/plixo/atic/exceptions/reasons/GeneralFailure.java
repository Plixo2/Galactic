package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Record;
import de.plixo.atic.lexer.Region;
import org.jetbrains.annotations.Nullable;

public final class GeneralFailure extends Failure {
    public GeneralFailure(@Nullable Region region, String message) {
        setRegion(region);
        setMessage(message);
    }
}
