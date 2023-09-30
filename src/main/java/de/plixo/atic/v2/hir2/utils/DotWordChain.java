package de.plixo.atic.v2.hir2.utils;

import de.plixo.atic.lexer.Node;
import lombok.Getter;

import java.util.List;

public class DotWordChain {

    @Getter
    private final List<String> list;

    public DotWordChain(Node node) {
        this.list = node.list("dotWordChain", "dotWordChainOpt", "id").stream().map(Node::getID)
                .toList();
    }

    public String asString() {
        return String.join(".", list);
    }
}
