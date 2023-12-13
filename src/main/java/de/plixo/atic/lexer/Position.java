package de.plixo.atic.lexer;

import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Location of a Token
 * @param file source file (can be null)
 * @param line source line
 */
public record Position(@Nullable File file, int line) {

    @Override
    public String toString() {
        if (file != null) {
            return file.getAbsolutePath() + ":" + (line + 1);
        }
        return "line " + line;
    }


    public Region toRegion() {
        return new Region(this, new Position(this.file, this.line));
    }
}
