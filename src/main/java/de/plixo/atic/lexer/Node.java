package de.plixo.atic.lexer;

import de.plixo.atic.exceptions.reasons.GrammarNodeFailure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
public class Node {
    @Getter
    private final @Nullable Record record;

    @Getter
    private final @Nullable String name;

    @Getter
    private final boolean isLeafNode;

    @Getter
    private final List<Node> children;

    @Getter
    Region region;

    public boolean has(String name) {
        for (Node child : children) {
            if (child.name != null && child.name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public Node get(String name) {
        for (Node child : children) {
            if (child.name != null && child.name.equalsIgnoreCase(name)) return child;
        }
        throw new GrammarNodeFailure("cant find child \"" + name + "\"", this).create();
    }

    public Node child() {
        return children.get(0);
    }

    public String getID() {
        if (Objects.equals("id", name)) {
            return Objects.requireNonNull(this.child().record).data();
        }
        final Node id = get("id");
        return Objects.requireNonNull(id.child().record).data();
    }

    public String getNumber() {
        if (Objects.equals("number", name)) {
            return Objects.requireNonNull(this.child().record).data();
        }
        final Node id = get("number");
        return Objects.requireNonNull(id.child().record).data();
    }

    public String getString() {
        if (Objects.equals("string", name)) {
            return Objects.requireNonNull(this.child().record).data();
        }
        final Node id = get("string");
        return Objects.requireNonNull(id.child().record).data();
    }

    public static Node fromSyntaxNode(File file, AutoLexer.SyntaxNode node) {
        return switch (node) {
            case AutoLexer.BranchNode branchNode -> {
                var children = branchNode.list().stream().map(ref -> Node.fromSyntaxNode(file, ref))
                        .toList();
                Region first = null;
                Region last = null;
                for (Node child : children) {
                    if (child.region != null) {
                        if (first == null) {
                            first = child.region;
                        }
                        last = child.region;
                    }
                }
                Region region = null;
                if (last != null) {
                    region = new Region(file, first.left(), last.right());
                }
                yield new Node(null, node.name(), false, children, region);
            }
            case AutoLexer.LeafNode leafNode -> {
                var data = leafNode.data();
                var left = new Position(data.position().line(), data.position().from(),
                        data.position().to());
                var right = new Position(data.position().line(), data.position().from(),
                        data.position().to());
                yield new Node(data, node.name(), true, new ArrayList<>(),
                        new Region(file, left, right));
            }
        };
    }

    public void fillPosition(File file) {
        var left = this.region.left();
        for (Node child : this.children()) {
            if (child.region == null) {
                child.region = new Region(file, left, left);
            }
            child.fillPosition(file);
            left = child.region.right();
        }
    }


    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean minify) {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "", minify);
        return "\n" + buffer;
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix, boolean minify) {

        if (!isLeafNode && minify) {
            if (children.size() == 1) {
                children.get(0).print(buffer, prefix + name + " > ", childrenPrefix, true);
                return;
            } else if (children.size() == 0) {
                return;
            }
        }
        StringBuilder bob = new StringBuilder();
        bob.append(prefix);
        if (isLeafNode) {
            assert record != null;
            bob.append("\"").append(record.data()).append("\"");
        } else {
            bob.append(name);
        }
        buffer.append(bob);
        int l = 80;
        //wtf
        var bobLength = new String(bob.toString().getBytes(), StandardCharsets.UTF_8).length();
        var diff = Math.max(l - bobLength, 10);
        buffer.append(" ".repeat(diff)).append(this.region);
        buffer.append('\n');
        for (Iterator<Node> it = children.iterator(); it.hasNext(); ) {
            Node next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ", minify);
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ", minify);
            }
        }
    }

    public List<Node> list(String list, String list2, String leaf) {
        final List<Node> collection = new ArrayList<>();
        walk(leaf, list, list2, this, collection);
        return collection;
    }

    public List<Node> list(String list, String leaf) {
        final List<Node> collection = new ArrayList<>();
        walk(leaf, list, this, collection);
        return collection;
    }

    private static void walk(String leaf, String list, Node node, List<Node> collection) {
        if (node.has(leaf)) {
            final Node element = node.get(leaf);
            collection.add(element);
        }
        if (node.has(list)) {
            final Node sub = node.get(list);
            walk(leaf, list, sub, collection);
        }
    }

    private static void walk(String leaf, String list, String list2, Node node,
                             List<Node> collection) {
        if (node.has(leaf)) {
            final Node element = node.get(leaf);
            collection.add(element);
        }
        if (node.has(list)) {
            final Node sub = node.get(list);
            walk(leaf, list, list2, sub, collection);
        }
        if (node.has(list2)) {
            final Node sub = node.get(list2);
            walk(leaf, list, list2, sub, collection);
        }
    }

}
