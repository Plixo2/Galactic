package de.plixo.galactic.parsing;

import de.plixo.galactic.lexer.Position;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.lexer.TokenRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for applying grammar rules on a List of tokens
 */
@RequiredArgsConstructor
public class Parser {
    private final TokenStream<TokenRecord> stream;

    /**
     * try to apply a rule.
     *
     * @param rule rule to apply
     * @return a 'SyntaxResult' covering all states of failure and
     * success of this function.
     */
    public SyntaxResult build(Grammar.Rule rule) {
        var startIndex = stream.index();
        val node = testRule(rule);
        if (stream.hasEntriesLeft() || node == null) {
            if (node == null && !stream.hasEntriesLeft()) {
                stream.setIndex(startIndex);
            }
            return new FailedRule(stream.left(), rule, null);
        }
        return node;
    }

    /**
     * Tests a rule.
     *
     * @param rule rule to apply
     * @return a 'SyntaxResult' covering all states of failure and
     * success of this function.
     */
    private @Nullable SyntaxResult testRule(Grammar.Rule rule) {
        for (var sentence : rule.sentences()) {
            final int index = stream.index();
            val node = testSentence(rule, sentence);
            if (node == null) {
                stream.setIndex(index);
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * Tests a sentence.
     *
     * @param rule     origin rule
     * @param sentence sentence to apply
     * @return a 'SyntaxResult' covering all states of failure and
     * success of this function.
     */
    private @Nullable SyntaxResult testSentence(Grammar.Rule rule, Grammar.Sentence sentence) {
        var nodes = new ArrayList<Node>();
        if (!stream.hasEntriesLeft()) {
            return null;
        }
        var leftToken = stream.current();
        var rightPos = stream.current().position().maxPosition();
        for (var entry : sentence.entries()) {
            if (!stream.hasEntriesLeft()) {
                return null;
            }
            rightPos = stream.current().position().maxPosition();
            switch (entry) {
                case Grammar.LiteralEntry literalEntry -> {
                    var token = stream.current();
                    var test = token.token().alias().equals(literalEntry.name());
                    if (!test) {
                        if (literalEntry.throwError()) {
                            return new FailedLiteral(stream.left(), rule, literalEntry.name(),
                                    token);
                        } else {
                            return null;
                        }
                    }
                    if (literalEntry.capture()) {
                        var region = token.position();
                        nodes.add(new Node(token, rule.name(), new ArrayList<>(), region));
                    }
                    stream.consume();
                }
                case Grammar.RuleEntry ruleEntry -> {
                    var child = testRule(ruleEntry.rule());
                    switch (child) {
                        case FailedLiteral syntaxError -> {
                            return syntaxError;
                        }
                        case FailedRule syntaxError -> {
                            return syntaxError;
                        }
                        case SyntaxMatch syntaxMatch -> {
                            var node = syntaxMatch.node();
                            nodes.add(node);
                            rightPos = node.region().right();
                        }
                        case null -> {
                            if (ruleEntry.throwError()) {
                                return new FailedRule(stream.left(), ruleEntry.rule(), rule);
                            }
                            return null;
                        }
                    }
                }
            }
        }
//        if (stream.hasEntriesLeft()) {
//            rightPos = stream.current().position();
//        }
        var region = new Region(leftToken.position().minPosition(), rightPos);
        return new SyntaxMatch(new Node(leftToken, rule.name(), nodes, region));
    }

    public static abstract sealed class SyntaxResult {

    }

    @AllArgsConstructor
    @Getter
    public static final class FailedRule extends SyntaxResult {
        List<TokenRecord> records;
        Grammar.Rule failedRule;
        @Nullable Grammar.Rule parentRule;
    }

    @AllArgsConstructor
    @Getter
    public static final class FailedLiteral extends SyntaxResult {
        List<TokenRecord> records;
        Grammar.Rule parentRule;
        String expectedLiteral;
        TokenRecord consumedLiteral;
    }

    @Getter
    @AllArgsConstructor
    public static final class SyntaxMatch extends SyntaxResult {
        private final Node node;
    }
}
