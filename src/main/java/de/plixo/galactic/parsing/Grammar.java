package de.plixo.galactic.parsing;

import de.plixo.galactic.lexer.TokenRecord;
import de.plixo.galactic.lexer.Tokenizer;
import de.plixo.galactic.lexer.tokens.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Grammar parser for parsing a special defined format.
 * The 'generate' Function will generate a 'RuleSet' with all rules and sentences.
 * Each Rule has different sentences, which are composed of different entries.
 * An Entry can either be a rule or a literal.
 */
public class Grammar {

    private final List<Consumer<RuleSet>> solveLater = new ArrayList<>();

    /**
     * Main function for generating and linking all rules
     *
     * @param rules List of raw string rules
     * @return a RuleSet
     */
    public RuleSet generate(Iterator<String> rules) {
        var ruleSet = new RuleSet();
        var tokenizer = new Tokenizer(notationTokens());
        var lineIndex = 0;
        while (rules.hasNext()) {
            var line = rules.next();
            if (line.strip().startsWith("//")) {
                continue;
            }
            var records = tokenizer.tokenize(line, null, lineIndex);
            var recordList = records.stream().filter(ref -> {
                if (ref.token() instanceof UnknownToken) {
                    throw ref.createException();
                }
                return !ref.ofType(WhiteSpaceToken.class);
            }).toList();
            var stream = new TokenStream<>(recordList);
            if (recordList.isEmpty()) {
                continue;
            }
            var rule = generateRule(stream);

            if (stream.hasEntriesLeft()) {
                throw stream.current().createException();
            }
            if (ruleSet.has(rule.name)) {
                throw stream.current().createException("name collision " + rule.name);
            }
            ruleSet.rules.put(rule.name, rule);
            lineIndex += 1;
        }
        solveLater.forEach(ref -> ref.accept(ruleSet));
        solveLater.clear();
        return ruleSet;
    }

    /**
     * Generates a new rule from a TokenStream
     *
     * @param records TokenStream
     * @return a Rule
     */
    private Rule generateRule(TokenStream<TokenRecord> records) {
        var name = expectWord(records.current());
        records.consume();
        if (!records.current().literal().equals(":=")) {
            throw records.current().createException();
        }
        records.consume();

        var rule = new Rule(name);

        while (records.hasEntriesLeft()) {
            var sentence = generateSentence(records);
            rule.sentences.add(sentence);
            records.consume();
        }

        if (records.hasEntriesLeft()) {
            throw records.current().createException();
        }

        return rule;
    }

    /**
     * Generates a new sentence from a TokenStream, composed of different entries
     *
     * @param stream TokenStream
     * @return a Sentence
     */
    private Sentence generateSentence(TokenStream<TokenRecord> stream) {
        var sentence = new Sentence();
        while (stream.hasEntriesLeft() && !stream.current().literal().equals("|")) {
            var record = stream.current();
            stream.consume();
            sentence.entries.add(switch (record.token()) {
                case StringToken ignored -> {
                    var ignoreToken = false;
                    if (stream.hasEntriesLeft()) {
                        ignoreToken = is(stream.current(), "?");
                        if (ignoreToken) stream.consume();
                    }
                    var throwError = false;
                    if (stream.hasEntriesLeft()) {
                        throwError = is(stream.current(), "!");
                        if (throwError) stream.consume();
                    }

                    var literal = record.literal();
                    yield new LiteralEntry(literal.substring(1, literal.length() - 1), !ignoreToken,
                            throwError);
                }
                case WordToken ignored -> {
                    var rule = record.literal();
                    var throwError = false;
                    if (stream.hasEntriesLeft()) {
                        throwError = is(stream.current(), "!");
                    }
                    if (throwError) stream.consume();
                    var ruleEntry = new RuleEntry(throwError);
                    this.solveLater.add((set) -> {
                        var ruleVal = set.get(rule);
                        if (ruleVal == null) {
                            throw record.createException("cant find " + rule);
                        }
                        ruleEntry.rule = ruleVal;
                    });
                    yield ruleEntry;
                }
                default -> {
                    throw record.createException();
                }
            });
        }

        return sentence;
    }

    private String expectWord(TokenRecord word) {
        if (!(word.token() instanceof WordToken)) {
            throw word.createException();
        }
        return word.literal().toLowerCase();
    }


    private boolean is(TokenRecord word, String literal) {
        return word.literal().equals(literal);
    }

    /**
     * All tokens used for parsing the grammar
     */

    private List<Token> notationTokens() {
        var tokens = new ArrayList<Token>();
        tokens.add(new CharToken('!'));
        tokens.add(new CharToken('?'));
        tokens.add(new CharToken('|'));
        tokens.add(new LiteralToken(":="));
        tokens.add(new WhiteSpaceToken());
        tokens.add(new WordToken());
        tokens.add(new StringToken());

        return tokens;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Rule {
        private final String name;
        private final List<Sentence> sentences = new ArrayList<>();

        @Override
        public String toString() {
            return "Rule{" + "name='" + name + '\'' + ", sentences=" + sentences + '}';
        }
    }

    @Getter
    public static class Sentence {
        private final List<Entry> entries = new ArrayList<>();

        @Override
        public String toString() {
            return "Sentence{" + "entries=" + entries + '}';
        }
    }

    public abstract sealed static class Entry {
    }

    @Getter
    @RequiredArgsConstructor
    public static final class RuleEntry extends Entry {
        private Rule rule;
        private final boolean throwError;

        @Override
        public String toString() {
            return "RuleEntry{" + "rule=" + rule.name + ", throwError=" + throwError + '}';
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static final class LiteralEntry extends Entry {
        private final String name;
        private final boolean capture;
        private final boolean throwError;

        @Override
        public String toString() {
            return "LiteralEntry{" + "name='" + name + '\'' + ", capture=" + capture +
                    ", throwError=" + throwError + '}';
        }
    }

    /**
     * a RuleSet where all rules are contained
     */
    public static class RuleSet {
        private final Map<String, Rule> rules = new HashMap<>();

        public @Nullable Rule get(String name) {
            return rules.get(name.toLowerCase());
        }

        public boolean has(String name) {
            return rules.containsKey((name.toLowerCase()));
        }
    }
}
