package de.plixo.atic.hir.parsing.records;

import de.plixo.atic.lexer.Node;
import de.plixo.atic.lexer.Region;

import java.util.ArrayList;
import java.util.List;

public record WordList(Region region, List<String> words) {

    public static WordList create(Node node) {
        var words = new ArrayList<String>();
        node.list("wordList", "wordListOpt", "id").forEach(ref -> words.add(ref.getID()));
        return new WordList(node.region(), words);
    }
}
