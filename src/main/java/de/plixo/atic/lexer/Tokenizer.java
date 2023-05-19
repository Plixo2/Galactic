package de.plixo.atic.lexer;

import de.plixo.atic.Token;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Tokenizer {
    @Getter
    private final List<Token> tokens;

    public Tokenizer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TokenResult apply(int line, @NonNull String text) {
        var records = new ArrayList<Record>();
        int charCount = 0;
        final int length = text.length();
        final StringBuilder capturedChars = new StringBuilder();
        while (charCount < length) {
            final String subString = text.substring(charCount);
            var matchedToken = tokens.stream().filter(f -> f.peek.test(subString)).findAny();
            if (matchedToken.isEmpty()) {
                return new TokenFailure(subString, charCount);
            }
            capturedChars.append(text.charAt(charCount));
            final int countCopy = charCount;
            charCount += 1;
            while (charCount < length) {
                capturedChars.append(text.charAt(charCount));
                charCount += 1;
                if (!matchedToken.get().capture.test(capturedChars.toString())) {
                    capturedChars.deleteCharAt(capturedChars.length() - 1);
                    charCount -= 1;
                    break;
                }
            }
            var position = new Position(line, countCopy + 1, charCount + 1);
            records.add(new Record(matchedToken.get(), capturedChars.toString(), position));
            capturedChars.setLength(0);
        }
        return new TokenSuccess(records);
    }

    public sealed interface TokenResult {

    }

    public record TokenSuccess(List<Record> records) implements TokenResult {

    }

    public record TokenFailure(String text, int charCount) implements TokenResult {

    }

    public static <T> void apply(@NonNull String text, @NonNull List<T> tokens,
                                 @NonNull TokenConsumer<T> consumer,
                                 @NonNull BiConsumer<Integer, String> onError,
                                 @NonNull BiFunction<T, String, Boolean> tokenPeekPredicate,
                                 @NonNull BiFunction<T, String, Boolean> tokenCapturePredicate) {

        int charCount = 0;
        final int length = text.length();
        final StringBuilder capturedChars = new StringBuilder();
        while (charCount < length) {
            final String subString = text.substring(charCount);
            final Optional<T> matchedToken =
                    tokens.stream().filter(f -> tokenPeekPredicate.apply(f, subString)).findAny();
            if (matchedToken.isEmpty()) {
                onError.accept(charCount, subString);
                break;
            }
            capturedChars.append(text.charAt(charCount));
            final int countCopy = charCount;
            charCount += 1;
            while (charCount < length) {
                capturedChars.append(text.charAt(charCount));
                charCount += 1;
                if (!tokenCapturePredicate.apply(matchedToken.get(), capturedChars.toString())) {
                    capturedChars.deleteCharAt(capturedChars.length() - 1);
                    charCount -= 1;
                    break;
                }
            }
            consumer.apply(matchedToken.get(), capturedChars.toString(), countCopy, charCount);
            capturedChars.setLength(0);
        }
    }

    public interface TokenConsumer<T> {
        void apply(T token, String data, int from, int to);
    }
}
