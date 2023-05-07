package de.plixo.lexer;

import de.plixo.lexer.exceptions.UnknownRuleException;
import de.plixo.lexer.tokenizer.Tokenizer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import static de.plixo.lexer.GrammarReader.GrammarToken.*;

public class GrammarReader {

    public static RuleSet loadFromString(String[] txt) {
        final List<Runnable> onFinalResolve = new ArrayList<>();
        final List<Rule> rules = new ArrayList<>();
        for (String line : txt) {
            final List<TokenRecord<GrammarToken>> list = new ArrayList<>();
            Tokenizer.apply(line, Arrays.asList(GrammarToken.values()), (token, data, from, to) -> {
                        list.add(new TokenRecord<>(token, data, from, to));
                    }, (character, begin) -> {

                    }, (token, subString) -> token.peek.asPredicate().test(subString),
                    (token, subString) -> token.capture.asPredicate().test(subString));
            list.removeIf(f -> f.token == GrammarToken.WHITESPACE);
            final TokenStream<TokenRecord<GrammarToken>> stream = new TokenStream<>(list);
            if (stream.size() == 0) {
                continue;
            }
            if (testToken(stream, COMMENT)) {
                continue;
            }

            final Rule rule = genRule(stream, (name, ref) -> onFinalResolve.add(() -> {
                final ArrayList<Rule> nameRuleSet = new ArrayList<>(rules);
                nameRuleSet.removeIf(ruleRef -> !ruleRef.name.equalsIgnoreCase(name));
                if (nameRuleSet.size() > 1) {
                    throw new UnknownRuleException("Found more than one definition of rule \"" + name + "\"");
                } else if (nameRuleSet.size() == 0) {
                    throw new UnknownRuleException("Unknown rule \"" + name + "\"");
                }
                ref.rule = nameRuleSet.get(0);
            }));
            rules.add(rule);
        }
        onFinalResolve.forEach(Runnable::run);
        return new RuleSet(rules);
    }


    private static Rule genRule(TokenStream<TokenRecord<GrammarToken>> stream, BiConsumer<String, Entry> delayedResolve) {
        final List<Sentence> sentences = new ArrayList<>();

        final String name = assertToken(stream, KEYWORD);
        consume(stream);
        assertToken(stream, ASSIGN);
        consume(stream);

        while (stream.hasEntriesLeft()) {
            final List<Entry> entries = new ArrayList<>();
            while (stream.hasEntriesLeft() && !testToken(stream, OR)) {
                final String data = stream.current().data;
                if (testToken(stream, KEYWORD)) {
                    final Entry ruleEntry = genPlaceholderEntry();
                    entries.add(ruleEntry);
                    delayedResolve.accept(data, ruleEntry);
                    stream.consume();
                    if (stream.hasEntriesLeft() && testToken(stream, CONCRETE)) {
                        stream.consume();
                        ruleEntry.isConcrete = true;
                    }
                } else if (testToken(stream, LITERAL)) {
                    final Entry e = genEntry(data.substring(1, data.length() - 1));
                    entries.add(e);
                    stream.consume();
                    if (stream.hasEntriesLeft() && testToken(stream, HIDDEN)) {
                        stream.consume();
                        e.isHidden = true;
                    }
                    if (stream.hasEntriesLeft() && testToken(stream, CONCRETE)) {
                        stream.consume();
                        e.isConcrete = true;
                    }
                } else {
                    throw new UnknownRuleException("Expected keyword or literal, but got " + stream.current());
                }
            }
            stream.consume();
            sentences.add(new Sentence(entries));
        }


        return new Rule(name, sentences);
    }

    private static boolean testToken(TokenStream<TokenRecord<GrammarToken>> stream, GrammarToken token) {
        if (!stream.hasEntriesLeft()) {
            throw new UnknownRuleException("Expected " + token.name() + ", but ran out of tokens");
        }
        return stream.current().token == token;
    }

    private static String assertToken(TokenStream<TokenRecord<GrammarToken>> stream, GrammarToken token) {
        if (!stream.hasEntriesLeft()) {
            throw new UnknownRuleException("Expected " + token.name() + ", but ran out of tokens");
        }
        if (stream.current().token != token) {
            throw new UnknownRuleException("Expected " + token.name() + ", but got " + stream.current());
        }
        return stream.current().data;
    }

    private static void consume(TokenStream<?> stream) {
        stream.consume();
    }

    @RequiredArgsConstructor
    public static class RuleSet {
        final List<Rule> rules;

        public Rule findRule(String name) {
            for (Rule rule : rules) {
                if (rule.name.equalsIgnoreCase(name)) {
                    return rule;
                }
            }
            return null;
        }
    }


    @RequiredArgsConstructor
    static class Rule {
        final String name;
        final List<Sentence> sentences;
    }

    @RequiredArgsConstructor
    static class Sentence {
        final List<Entry> entries;
    }

    static class Entry {
        @Nullable String literal = null;
        @Nullable Rule rule = null;
        boolean isHidden = false;
        boolean isConcrete = false;

        boolean isLiteral() {
            return literal != null;
        }

    }

    private static Entry genEntry(String literal) {
        final Entry entry = new Entry();
        entry.literal = literal;
        return entry;
    }

    private static Entry genPlaceholderEntry() {
        return new Entry();
    }

    public enum GrammarToken {
        KEYWORD("[a-zA-Z]", "\\w+$"),
        WHITESPACE("\\s", "\\s*"),
        ASSIGN(":=", "(:|:=)"),
        LITERAL("\"", "(" + "(\\\"[^\\\"]*\\\")|(\\\"[^\\\"]*))"),
        OR("\\|", "\\|"),
        HIDDEN("\\?", "\\?"),
        CONCRETE("\\!", "\\!"),
        COMMENT("//", "//.*"),


        ;
        public final Pattern peek;
        public final Pattern capture;

        GrammarToken(String peek, String capture) {
            this.peek = Pattern.compile("^" + peek, Pattern.MULTILINE);
            this.capture = Pattern.compile("^" + capture + "$", Pattern.MULTILINE);
        }
    }

    @AllArgsConstructor
    private static class TokenRecord<T> {
        public final T token;
        public final String data;
        public final int from;
        public final int to;

        @Override
        public String toString() {
            return "TokenRecord{" + "token=" + token + ", data='" + data + '\'' + ", from=" + from + ", to=" + to + '}';
        }
    }

}