package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Record;
import de.plixo.atic.lexer.Region;

public class UnexpectedTokenError extends LanguageError {

    public UnexpectedTokenError(Region region, Record record) {
        super(region, "Unexpected token " + record);
    }
}
