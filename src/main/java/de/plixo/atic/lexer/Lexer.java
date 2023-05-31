package de.plixo.atic.lexer;

import de.plixo.atic.Language;
import de.plixo.atic.Token;
import de.plixo.atic.exceptions.reasons.TokenFailure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Lexer {
    private final GrammarReader.RuleSet ruleSet;
    private final AutoLexer engine;

    private final GrammarReader.Rule entry;

    public Lexer(String grammar, String entry) {
        ruleSet = GrammarReader.loadFromString(grammar.lines().toArray(String[]::new));
        engine = new AutoLexer();
        this.entry = Objects.requireNonNull(ruleSet.findRule(entry));
    }

    private final static Tokenizer TOKENIZER = new Tokenizer(Arrays.asList(Token.values()));

    public Node buildTree(File file, String src) {
        var records = generateTokens(src);
        records.removeIf(
                record -> record.token() == Token.WHITESPACE || record.token() == Token.COMMENT);

        var node = engine.reverseRule(entry, records);

        return switch (node) {
            case AutoLexer.SyntaxMatch syntaxMatch -> {
                var converted = Node.fromSyntaxNode(file, syntaxMatch.node());
                converted.fillPosition(file);
                yield converted;
            }
            case AutoLexer.FailedLiteral failedLiteral -> {
                var s = "Failed to match literal " + failedLiteral.expectedLiteral + " in rule " +
                        failedLiteral.parentRule.name();

                s += ", instead found token from type " + failedLiteral.consumedLiteral.token().name();
                s += " at " + failedLiteral.consumedLiteral.position().toString();
                throw new NullPointerException(s);
            }
            case AutoLexer.FailedRule failedRule -> {
                var s = "Failed to match rule " + failedRule.failedRule.name() + " in rule " +
                        failedRule.parentRule.name();
                if (!failedRule.records.isEmpty()) {
                s += " at " + failedRule.records.get(0).position().toString();
                }
                for (Record record : failedRule.records) {
                    s += record + "\n";
                }
                throw new NullPointerException(s);
            }
            case null -> {
                var s = "Failed to match rule " + entry.name();
                throw new NullPointerException(s);
            }
        };
    }

    private List<Record> generateTokens(String src) {
        var lines = src.lines().iterator();
        var tokens = new ArrayList<Record>();

        var line_counter = 1;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<List<Record>>>();
            while (lines.hasNext()) {
                var line = lines.next();
                var line_count = line_counter;
                Language.TASK_CREATED += 1;
                futures.add(executor.submit(
                        () -> new ArrayList<>(switch (Lexer.TOKENIZER.apply(line_count, line)) {
                            case Tokenizer.TokenFailure(var text, var ignored) ->
                                    throw new NullPointerException(
                                            "failed to capture token " + text);
                            case Tokenizer.TokenSuccess(var records) -> records;
                        })));

                line_counter += 1;
            }

            futures.forEach(ref -> {
                try {
                    tokens.addAll(ref.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        tokens.add(
                new Record(Token.END_OF_FILE, "END - OF - FILE", new Position(line_counter, 0, 0)));
        return tokens;
    }

}
