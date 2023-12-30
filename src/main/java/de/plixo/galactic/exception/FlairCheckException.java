package de.plixo.galactic.exception;


import de.plixo.galactic.lexer.Region;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
            var minLine = position.left().line();
            var maxLine = position.right().line();
            try {
                var src = FileUtils.readFileToString(file, StandardCharsets.UTF_8).lines().toList();
                if (minLine >= 0 && minLine < src.size() && maxLine >= 0 && maxLine < src.size()) {
                    var trueMin = Math.min(minLine, maxLine);
                    var trueMax = Math.max(minLine, maxLine);
                    lines.add("");
                    int index = trueMin;
                    do {
                        String line = STR."\{index + 1} \{src.get(index)}";
                        lines.add(line);
                        index++;
                    } while (index < trueMax);
                    lines.add("");
                }

            } catch (IOException e) {
                throw new RuntimeException("cant open file for error parsing", e);
            }
            lines.add(STR."in file:///\{file.getAbsolutePath().replace("\\", "/")}:\{minLine + 1}");
        }
        return String.join("\n", lines);
    }
}
