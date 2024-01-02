package de.plixo.galactic.lexer;

import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Location of a Token
 *
 * @param file source file (can be null)
 * @param line source line
 */
public record Position(@Nullable File file, int line, int character) {

    @Override
    public String toString() {
        if (file != null) {
            return STR."file:///\{file.getAbsolutePath().replace("\\", "/")}:\{line + 1}:\{character + 1}";
        }
        return STR."line \{line}, character \{character}";
    }


    public Region toRegion() {
        return new Region(this, new Position(this.file, this.line, this.character + 1));
    }
}
