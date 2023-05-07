package de.plixo.atic.exceptions;

import de.plixo.atic.lexer.Region;

public class UnknownImportError extends LanguageError {
    public UnknownImportError(Region region, String import_, String path) {
        super(region, "Cant find import \"" + import_ + "\" in path \"" + path + "\"");
    }
}
