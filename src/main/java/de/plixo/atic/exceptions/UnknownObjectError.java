package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Region;

public class UnknownObjectError extends LanguageError {

    public UnknownObjectError(Region region, String object, String path) {
        super(region, "Cant find object \"" + object + " in \"" + path + "\"");
    }

}
