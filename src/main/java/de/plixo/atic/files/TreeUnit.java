package de.plixo.atic.files;

import de.plixo.atic.lexer.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

public final class TreeUnit extends TreeEntry {
    @Getter
    @Accessors(fluent = true)
    private final File file;

    @Getter
    @Setter
    @Accessors(fluent = false)
    private Node root;

    public TreeUnit(String localName, String name, File file, Node node) {
        super(localName, name);
        this.file = file;
        this.root = node;
    }

    @Override
    public String toString() {
        return "Unit " + localName();
    }


}
