package de.plixo.atic.lexer;

import de.plixo.atic.Language;
import de.plixo.atic.Token;
import de.plixo.atic.exceptions.UnexpectedTokenError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Lexer {
    private final GrammarReader.RuleSet ruleSet;
    private final AutoLexer<Record> engine;

    public Lexer(String grammar) {
        ruleSet = GrammarReader.loadFromString(grammar.lines().toArray(String[]::new));
        engine = new AutoLexer<>((str, token) -> token.token().alias.equals(str), record -> {
            //throw new UnexpectedTokenError(Region.fromPosition(record.position()), record);
        });
    }

    private final static Tokenizer TOKENIZER = new Tokenizer(Arrays.asList(Token.values()));

    public Node buildTree(String src) {
        var records = generateTokens(src);
        records.removeIf(record -> record.token() == Token.WHITESPACE || record.token() == Token.COMMENT);
        var node = engine.reverseRule(ruleSet, "unit", records);
        if (node == null) {
            var first = records.get(0);
            throw new UnexpectedTokenError(Region.fromPosition(first.position()), first);
        }
        var converted = Node.fromSyntaxNode(node);
        converted.fillPosition();
//        try {
//            FileUtils.write(new File("resources/ast.txt"), converted.toString(false), Charset.defaultCharset());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return converted;
    }

    private List<Record> generateTokens(String src) {
        var startTime = System.currentTimeMillis();
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
        System.out.println("Token Total " + (System.currentTimeMillis() - startTime) + "ms");
        return tokens;
    }

}
