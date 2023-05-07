package de.plixo.hir.parsing.records;

import de.plixo.atic.lexer.Node;

import java.util.ArrayList;
import java.util.List;

public record WordList(List<String> words) {

    public static WordList create(Node node) {
        var words = new ArrayList<String>();
        node.list("wordList", "wordListOpt", "id").forEach(ref -> {
            words.add(ref.getID());
        });
        return new WordList(words);
    }
}
