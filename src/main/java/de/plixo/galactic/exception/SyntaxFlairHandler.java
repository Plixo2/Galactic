package de.plixo.galactic.exception;

import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.parsing.Grammar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class SyntaxFlairHandler {
    private final List<SyntaxFlair> records = new ArrayList<>();

    public void add(SyntaxFlair record) {
        records.add(record);
    }

    public void handle() {
        if (records.isEmpty()) {
            return;
        }

        var strings = records.stream().map(ref -> switch (ref) {
            case FailedRule failedRule -> {
                if (failedRule.records.isEmpty()) {
                    yield STR."Failed to parse \{failedRule.failedRule.name()}";
                } else {
                    var failedRecord = failedRule.records.get(0);
                    yield STR."Failed \{failedRule.failedRule.name()}: \{failedRecord.errorMessage()}";
                }
            }
            case FailedLiteral failedLiteral -> failedLiteral.consumedLiteral.errorMessage();
        }).toList();
        var msg = String.join("\n", strings);
        throw new FlairException(STR."\n\{msg}");
    }


    public static abstract sealed class SyntaxFlair {

    }

    @AllArgsConstructor
    public static final class FailedRule extends SyntaxFlair {
        List<TokenRecord> records;
        Grammar.Rule failedRule;
        @Nullable Grammar.Rule parentRule;
    }

    @AllArgsConstructor
    public static final class FailedLiteral extends SyntaxFlair {
        List<TokenRecord> records;
        Grammar.Rule parentRule;
        String expectedLiteral;
        TokenRecord consumedLiteral;
    }


}
