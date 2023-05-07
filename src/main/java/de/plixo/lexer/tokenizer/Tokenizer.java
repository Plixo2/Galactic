package de.plixo.lexer.tokenizer;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tokenizer {
    public static <T> List<T> apply(@NonNull String text,
                                 @NonNull Collection<T> tokens,
                                 @NonNull TokenConsumer<T> consumer,
                                 @NonNull BiConsumer<Integer, String> onError,
                                 @NonNull BiFunction<T, String, Boolean> tokenPeekPredicate,
                                 @NonNull BiFunction<T, String, Boolean> tokenCapturePredicate) {

        var records = new ArrayList<T>();
        int charCount = 0;
        final int length = text.length();
        final StringBuilder capturedChars = new StringBuilder();
        while (charCount < length) {
            final String subString = text.substring(charCount);
            final Optional<T> matchedToken = tokens.stream().filter(f -> tokenPeekPredicate.apply(f, subString))
                    .findAny();
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
        return records;
    }
    public interface TokenConsumer<T> {
        void apply(T token, String data, int from, int to);
    }
}
