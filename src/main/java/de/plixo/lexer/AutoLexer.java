package de.plixo.lexer;

import de.plixo.lexer.exceptions.UnknownRuleException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class AutoLexer<T> {

    final BiPredicate<String, T> tokenTest;
    final Consumer<T> onError;

    public SyntaxNode<T> reverseRule(GrammarReader.RuleSet ruleSet, String entry, List<T> tokens) {
        final GrammarReader.Rule rule = ruleSet.findRule(entry);
        if (rule == null) {
            throw new UnknownRuleException("Unknown entry rule \"" + entry + "\"");
        }
        val stream = new TokenStream<>(tokens);
        val node = testRule(rule, stream);
        if (stream.hasEntriesLeft()) {
            return null;
        }
        return node;
    }

    @AllArgsConstructor
    private static class DelayNode<T> {
        SyntaxNode<T> node;
        int index;
    }


    private SyntaxNode<T> testRule(GrammarReader.Rule rule, TokenStream<T> stream) {
//        Thread[] threads = new Thread[rule.sentences.size()];
//        DelayNode[] objects = new DelayNode[rule.sentences.size()];
//        for (int i = 0; i < rule.sentences.size(); i++) {
//            var sentence = rule.sentences.get(i);
//            int finalI = i;
//            threads[i] = Thread.startVirtualThread(() -> {
//                var newStream = stream.copy();
//                val node = testSentence(rule, sentence, newStream);
//                if (node == null) {
//                    objects[finalI] = null;
//                } else {
//                    node.name = rule.name;
//                    if (newStream.hasEntriesLeft()) node.data = newStream.current();
//                    objects[finalI] = new DelayNode<>(node, newStream.index);
//                }
//            });
//        }
//        for (int i = 0; i < rule.sentences.size(); i++) {
//            var thread = threads[i];
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                //throw new RuntimeException(e);
//            }
//            var object = objects[i];
//            if (object != null) {
//                for (i++; i < rule.sentences.size(); i++) {
//                    threads[i].interrupt();
//                }
//                stream.index = object.index;
//                return object.node;
//            }
//        }
//        return null;



        for (GrammarReader.Sentence sentence : rule.sentences) {
            final int index = stream.index();
            val node = testSentence(rule, sentence, stream);
            if (node == null) {
                stream.setIndex(index);
            } else {
                node.name = rule.name;
                if (stream.hasEntriesLeft())
                    node.data = stream.current();
                return node;
            }
        }
        return null;
    }

    private SyntaxNode<T> testSentence(GrammarReader.Rule rule, GrammarReader.Sentence sentence,
                                       TokenStream<T> stream) {
        final List<SyntaxNode<T>> nodes = new ArrayList<>();
        for (int i = 0; i < sentence.entries.size(); i++) {
            final GrammarReader.Entry entry = sentence.entries.get(i);
            if (!stream.hasEntriesLeft()) {
                return null;
            }
            if (entry.isLiteral()) {
                final T token = stream.current();
                if (!tokenTest.test(entry.literal, token)) {
                    if (entry.isConcrete) {
                        onError.accept(token);
                        while (stream.hasEntriesLeft()) {
                            System.out.println("left " + stream.current());
                            stream.consume();
                        }
                        throw new UnknownRuleException(
                                "Failed to capture keyword " + entry.literal + " in " + rule.name);
                    }
                    return null;
                }
                if (!entry.isHidden) {
                    nodes.add(genLeaf(token));
                }
                stream.consume();
            } else {
                if (entry.rule == null) {
                    throw new NullPointerException();
                }
                final SyntaxNode<T> child = testRule(entry.rule, stream);
                if (child == null) {
                    if (entry.isConcrete) {
                        final T token = stream.current();
                        onError.accept(token);
                        while (stream.hasEntriesLeft()) {
                            System.out.println("left " + stream.current());
                            stream.consume();
                        }
                        throw new UnknownRuleException(
                                "Failed to capture rule " + entry.rule.name + " in " + rule.name);
                    }
                    return null;
                }
                nodes.add(child);
            }
        }

        return genNode(nodes);
    }

    private SyntaxNode<T> genLeaf(T data) {
        return new LeafNode(data);
    }

    private SyntaxNode<T> genNode(List<SyntaxNode<T>> list) {
        final SyntaxNode<T> syntaxNode = new SyntaxNode<>();
        syntaxNode.list = list;
        return syntaxNode;
    }

    public static class SyntaxNode<T> {
        public String name;
        public List<SyntaxNode<T>> list = new ArrayList<>();
        public T data;
    }

    @RequiredArgsConstructor
    public class LeafNode extends SyntaxNode<T> {
        public final T data;
    }

}
