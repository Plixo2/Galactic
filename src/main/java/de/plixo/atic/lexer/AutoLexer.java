package de.plixo.atic.lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoLexer {


    public @Nullable SyntaxResult reverseRule(GrammarReader.Rule entry, List<Record> tokens) {
        val stream = new TokenStream<>(tokens);
        val node = testRule(entry, stream);
        if (stream.hasEntriesLeft()) {
            return null;
        }
        return node;
    }

    private @Nullable SyntaxResult testRule(GrammarReader.Rule rule, TokenStream<Record> stream) {
        for (GrammarReader.Sentence sentence : rule.sentences) {
            final int index = stream.index();
            val node = testSentence(rule, sentence, stream);
            if (node == null) {
                stream.setIndex(index);
            } else {
                return node;
            }
        }
        return null;
    }

    private @Nullable SyntaxResult testSentence(GrammarReader.Rule rule,
                                                GrammarReader.Sentence sentence,
                                                TokenStream<Record> stream) {
        final List<SyntaxNode> nodes = new ArrayList<>();
        for (var entry : sentence.entries) {
            if (!stream.hasEntriesLeft()) {
                return null;
            }
            if (entry.isLiteral()) {
                var token = stream.current();
                var test = token.token().alias.equals(entry.literal);
                if (!test) {
                    if (entry.isConcrete) {
                        var left = new ArrayList<Record>();
                        while (stream.hasEntriesLeft()) {
                            left.add(stream.current());
                            stream.consume();
                        }
                        return new FailedLiteral(left, rule, entry.literal , token);
                    } else {
                        return null;
                    }
                }
                if (!entry.isHidden) {
                    nodes.add(new LeafNode(rule.name(), token));
                }
                stream.consume();
            } else {
                var child = testRule(Objects.requireNonNull(entry.rule), stream);
                switch (child) {
                    case FailedLiteral syntaxError -> {
                        return syntaxError;
                    }
                    case FailedRule syntaxError -> {
                        return syntaxError;
                    }
                    case SyntaxMatch syntaxMatch -> nodes.add(syntaxMatch.node());
                    case null -> {
                        if (entry.isConcrete) {
                            var left = new ArrayList<Record>();
                            while (stream.hasEntriesLeft()) {
                                left.add(stream.current());
                                stream.consume();
                            }
                            return new FailedRule(left, entry.rule, rule);
                        }
                        return null;
                    }
                }
            }
        }
        return new SyntaxMatch(new BranchNode(rule.name(), nodes));
    }

    public static abstract sealed class SyntaxResult {

    }

    @AllArgsConstructor
    public static final class FailedRule extends SyntaxResult {
        List<Record> records;
        GrammarReader.Rule failedRule;
        GrammarReader.Rule parentRule;
    }

    @AllArgsConstructor
    public static final class FailedLiteral extends SyntaxResult {
        List<Record> records;
        GrammarReader.Rule parentRule;
        String expectedLiteral;
        Record consumedLiteral;
    }

    @AllArgsConstructor
    public static final class SyntaxMatch extends SyntaxResult {
        @Getter
        private final SyntaxNode node;
    }

    @AllArgsConstructor
    public static abstract sealed class SyntaxNode permits BranchNode, LeafNode {
        @Getter
        private final String name;
    }

    public static final class LeafNode extends SyntaxNode {
        @Getter
        private final Record data;

        public LeafNode(String name, Record data) {
            super(name);
            this.data = data;
        }
    }

    public static final class BranchNode extends SyntaxNode {
        @Getter
        private final List<SyntaxNode> list;

        public BranchNode(String name, List<SyntaxNode> list) {
            super(name);
            this.list = list;
        }
    }

    public interface ErrorFunction<T> {
        RuntimeException get(T token, List<T> left, GrammarReader.Rule rule, String literal,
                             boolean isLiteral);
    }
}
