package de.plixo.atic.files;

import de.plixo.atic.lexer.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public sealed abstract class TreeEntry permits TreeEntry.TreePath, TreeEntry.TreeUnit {
    @Getter
    @Accessors(fluent = true)
    private final String localName;

    @Getter
    @Accessors(fluent = true)
    private final String name;

    public TreeEntry(String localName, String name) {
        this.localName = localName;
        this.name = name;
    }


    public static final class TreeUnit extends TreeEntry {
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

    public static final class TreePath extends TreeEntry {

        @Getter
        @Accessors(fluent = true)
        private final List<TreeEntry> entries = new ArrayList<>();

        public TreePath(String localName, String name) {
            super(localName, name);
        }

        public void addEntry(TreeEntry entry) {
            this.entries.add(entry);
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder(50);
            prettyPrint(this, buffer, "", "");
            return buffer.toString();
        }

        private static void prettyPrint(TreeEntry entry, StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            buffer.append(entry.localName());
            buffer.append('\n');
            if (entry instanceof TreePath path) {
                for (Iterator<TreeEntry> it = path.entries.iterator(); it.hasNext(); ) {
                    var next = it.next();
                    if (it.hasNext()) {
                        prettyPrint(next, buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                    } else {
                        prettyPrint(next, buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                    }
                }
            }
        }
    }

}
