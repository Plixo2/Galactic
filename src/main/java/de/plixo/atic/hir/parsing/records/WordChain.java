package de.plixo.atic.hir.parsing.records;

import de.plixo.atic.lexer.Node;

import java.util.ArrayList;
import java.util.List;

public record WordChain(List<String> words) {

    public static WordChain create(Node node) {
        var words = new ArrayList<String>();
        node.list("wordChain", "wordChainOpt", "id").forEach(ref -> words.add(ref.getID()));
        return new WordChain(words);
    }
}
