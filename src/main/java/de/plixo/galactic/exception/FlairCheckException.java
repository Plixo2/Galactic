package de.plixo.galactic.exception;


import de.plixo.galactic.lexer.Region;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This Error represents a faulty programm. Unlike a FlairException, this (mostly) an internal error.
 */
public class FlairCheckException extends FlairException {
    private final Region position;
    private final FlairKind kind;
    private final @Nullable String message;

    public FlairCheckException(Region position, FlairKind kind, String message) {
        super(toString(position, kind, message));
        this.position = position;
        this.kind = kind;
        this.message = message;
    }

    public FlairCheckException(Region position, FlairKind kind) {
        this(position, kind, null);
    }

    private static String toString(Region position, FlairKind kind, @Nullable String message) {
        if (message == null) return STR."\{kind.toString()} at \n\{position.right().toString()} ";
        return STR."\n\{kind.toString()}: \{message} at \n\{position.right().toString()} ";
    }

    public String prettyPrint() {
        var file = position.left().file();
        var lines = new ArrayList<String>();
        lines.add(STR."\{message} (\{kind})");

        if (file != null) {
            var minPosition = position.minPosition();
            var maxPosition = position.maxPosition();
            var minLine = minPosition.line();
            var maxLine = maxPosition.line();
            try {
                var src = FileUtils.readFileToString(file, StandardCharsets.UTF_8).lines().toList();
                if (minLine >= 0 && minLine < src.size() && maxLine >= 0 && maxLine < src.size()) {
                    lines.add("");
                    int index = minLine;

                    var startLength = STR."\{index + 1}: ";
                    boolean sameLine = minPosition.line() == maxPosition.line();
                    if (sameLine) {
                        var min = Math.min(minPosition.character(), maxPosition.character());
                        var delta = Math.max(Math.abs(maxPosition.character() - minPosition.character()), 1);
                        lines.add(STR."\{" ".repeat(min + startLength.length())}\{"v".repeat(delta)} here");
                    } else {
                        lines.add(STR."\{" ".repeat(minPosition.character() + startLength.length())}v here");
                    }
                    do {
                        String line = STR."\{index + 1}: \{src.get(index)}";
                        lines.add(line);
                        index++;
                    } while (index <= maxLine);
                    if (!sameLine) {
                        var endLength = STR."\{index + 1}: ";
                        lines.add(STR."\{" ".repeat(maxPosition.character() + endLength.length())}^ until here");
                    }
                    lines.add("");
                }

            } catch (IOException e) {
                throw new RuntimeException("cant open file for error parsing", e);
            }
            lines.add(STR."in file:///\{file.getAbsolutePath().replace("\\", "/")}:\{minLine + 1}:\{
                    minPosition.character() + 1}");
        }
        return String.join("\n", lines);
    }
}
