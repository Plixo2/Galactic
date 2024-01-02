package de.plixo.galactic.lexer;

import de.plixo.galactic.lexer.tokens.Token;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts an input string into a stream of tokens
 */
@AllArgsConstructor
public class Tokenizer {
    private final List<Token> tokens;

    /**
     * Creates tokens from a string from a file, by splitting all the lines.
     * This can be made parallel in the future.
     *
     * @param sourceFile source file for insights (can be null)
     * @param src        file source
     * @return generated tokens
     */
    public List<TokenRecord> fromFile(@Nullable File sourceFile, String src) {
        var tokens = new ArrayList<TokenRecord>();
        var iterator = src.lines().iterator();
        var index = 0;
        while (iterator.hasNext()) {
            var line = iterator.next();
            tokens.addAll(tokenize(line, sourceFile, index));
            index += 1;
        }
        return tokens;
    }

    /**
     * checks all tokens to see what fits the first character.
     * Then test everything again to see if it fits the rest of the text.
     * Then the function creates a record of the first fitting token and
     * continues to match the rest of the string.
     *
     * @param string the string to make tokens out of
     * @param file   to source file (can be null)
     * @param line   source line
     * @return generated records
     */
    public List<TokenRecord> tokenize(String string, @Nullable File file, int line) {
        if (string.isEmpty()) {
            return new ArrayList<>();
        }
        var records = new ArrayList<TokenRecord>();
        var matchingStartTokens = new ArrayList<Token>(5);
        var length = string.length();
        for (int index = 0; index < length; index++) {
            var position = new Position(file, line, index);
            var currentChar = string.charAt(index);
            for (Token token : tokens) {
                if (token.startsWith(currentChar)) {
                    matchingStartTokens.add(token);
                }
            }
            boolean matched = false;
            for (var matchingStart : matchingStartTokens) {
                var next = matchingStart.matches(string, index);
                if (next < 0) {
                    continue;
                }
                var literal = string.substring(index, Math.min(next, length));
                records.add(new TokenRecord(matchingStart, literal, position));
                index = next - 1;
                matched = true;
                break;
            }
            if (!matched) {
                var literal = string.substring(index, Math.min(index + 1, length));
                records.add(new TokenRecord(Token.unknownToken(), literal, position));
            }
            matchingStartTokens.clear();
        }

        return records;
    }
}
