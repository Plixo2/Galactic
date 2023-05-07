package de.plixo.atic.files;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class TreePath extends TreeEntry {

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
