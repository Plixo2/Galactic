package de.plixo.atic.exceptions;

import de.plixo.atic.exceptions.reasons.Failure;
import de.plixo.atic.lexer.Region;

public class LangError extends RuntimeException {

    public LangError(Failure failure) {
        super("Language error " + failure.getClass() + "\n" + message(failure),
                failure.internalError());
    }

    private static String message(Failure failure) {
        var bob = new StringBuilder();
        if (failure.message() != null) {
            bob.append(failure.message()).append(" ");
        }
        if (failure.region() != null) {
            bob.append("at ").append(createMsg(failure.region())).append(" ");
        } if (failure.file() != null) {
            bob.append("for file ").append(failure.file().getAbsolutePath());
        }
        if (failure.node() != null) {
            bob.append("for node ").append(failure.node().toString());
        }
        return bob.toString();
    }

    private static String createMsg(Region region) {
        var path = region.file().getAbsolutePath();
        return path + ":" + (region.left().line()) + ":" + (region.left().from());
    }
}
