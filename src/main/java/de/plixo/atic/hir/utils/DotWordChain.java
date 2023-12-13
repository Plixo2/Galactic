package de.plixo.atic.hir.utils;

import de.plixo.atic.parsing.Node;
import de.plixo.atic.tir.ObjectPath;
import lombok.Getter;

import java.util.ArrayList;
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

    public ObjectPath asObjectPath() {
        return new ObjectPath(new ArrayList<>(list));
    }
}
