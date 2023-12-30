package de.plixo.galactic.exception;

import de.plixo.galactic.lexer.TokenRecord;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects different Errors and throws them at the end.
 * Used in the Lexer Stage
 */
@Getter
public class TokenFlairHandler {
    private final List<TokenRecord> records = new ArrayList<>();

    public void add(TokenRecord record) {
        records.add(record);
    }

    public void handle() {
        var uniqueRecords = makeUnique();
        if (uniqueRecords.isEmpty()) {
            return;
        }

        var msg = String.join("\n", uniqueRecords.stream().map(TokenRecord::errorMessage).toList());
        throw new FlairException("\n" + msg);
    }

    private List<TokenRecord> makeUnique() {
        List<TokenRecord> unique = new ArrayList<>();
        for (TokenRecord record : records) {
            var contains = false;

            for (var existing : unique) {
                var file = existing.position().file();
                if (file == record.position().file()) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                unique.add(record);
            }
        }
        return unique;
    }
}
