package de.plixo.atic.lexer;

import de.plixo.atic.exceptions.FailedTokenCaptureError;
import de.plixo.atic.exceptions.UnexpectedTokenError;
import de.plixo.lexer.AutoLexer;
import de.plixo.lexer.GrammarReader;
import de.plixo.lexer.tokenizer.Tokenizer;

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
            throw new UnexpectedTokenError(Region.fromPosition(record.position()), record);
        });
    }

    List<Token> tokenDefinitions = Arrays.asList(Token.values());

    public Node buildTree(String src) {
        var records = generateTokens(tokenDefinitions, src);
        records.removeIf(
                record -> record.token() == Token.WHITESPACE || record.token() == Token.COMMENT);
        var eof = records.get(records.size() - 1);
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

    private List<Record> generateTokens(List<Token> definitions, String src) {
        var startTime = System.currentTimeMillis();
        var lines = src.lines().iterator();
        var tokens = new ArrayList<Record>();


        var line_counter = 1;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<List<Record>>>();
            while (lines.hasNext()) {
                var line = lines.next();
                var line_count = line_counter;
                futures.add(executor.submit(() -> {
                    var localTokens = new ArrayList<Record>();
                    Tokenizer.apply(line, definitions, (token, data, from, to) -> {
                                localTokens.add(new Record(token, data,
                                        new Position(line_count, from + 1, to + 1)));
                            }, (character, string) -> {
                                var position = new Position(line_count, character, character + 1);
                                throw new FailedTokenCaptureError(Region.fromPosition(position), string);
                            }, (token, string) -> token.peek.test(string),
                            (token, string) -> token.capture.test(string));
                    return localTokens;
                }));

                line_counter += 1;
            }

            futures.forEach(ref -> {
                try {
                    tokens.addAll(ref.get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("e.getCause() = " + e.getCause());
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
