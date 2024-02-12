package de.plixo.galactic.parsing;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.lexer.TokenRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main result of the parsing stage.
 */
@Getter
@RequiredArgsConstructor
public class Node {
    /**
     * First token upstream, or the final token
     */
    private final TokenRecord record;
    /**
     * Name of the node
     */
    private final String name;
    /**
     * Node children
     */
    private final List<Node> children;
    /**
     * Region over the whole node region
     */
    private final Region region;


    /**
     * Gets a Node with a given name, will fail if not found
     *
     * @param name name of the node
     * @return given node with a name
     */
    public Node get(String name) {
        for (var child : children) {
            if (child.name.equalsIgnoreCase(name)) {
                return child;
            }
        }
        throw record.createException(STR."expected Node \{name} in \{this.name}");
    }

    /**
     * Tests if the node contains a child with the given name
     *
     * @param name name of the node
     * @return true if the node contains a child with the given name
     */
    public boolean has(String name) {
        for (var child : children) {
            if (child.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void assertType(String name) {
        if (!this.name.equalsIgnoreCase(name)) {
            throw record.createException(STR."expected Node \{name} in \{this.name}");
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return STR."\n\{buffer}";
    }

    public String getID() {
        if (Objects.equals("id", name)) {
            return Objects.requireNonNull(this.child().record).literal();
        }
        final Node id = get("id");
        return Objects.requireNonNull(id.child().record).literal();
    }

    public Region getIDRegion() {
        if (Objects.equals("id", name)) {
            return this.region;
        }
        final Node id = get("id");
        return Objects.requireNonNull(id).region();
    }

    public String getNumber() {
        if (Objects.equals("number", name)) {
            return Objects.requireNonNull(child().record).literal();
        }
        final Node id = get("number");
        return Objects.requireNonNull(id.child().record).literal();
    }

    public String getString() {
        if (Objects.equals("string", name)) {
            return Objects.requireNonNull(child().record).literal();
        }
        final Node id = get("string");
        return Objects.requireNonNull(id.child().record).literal();
    }

    public Node child() {
        return children.get(0);
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


    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {

        var bob = new StringBuilder();
        bob.append(prefix);
        if (children.isEmpty()) {
            bob.append("\"").append(record.literal()).append("\"");
        } else {
            bob.append(name);
        }
        buffer.append(bob);
        var offsetLength = 80;
        var bobLength = new String(bob.toString().getBytes(),
                StandardCharsets.UTF_8).length(); //used for formatting
        var diff = Math.max(offsetLength - bobLength, 10);

        buffer.append(" ".repeat(diff));
        buffer.append(this.region().minLine());
        buffer.append(" - ").append(this.region().maxLine());
        buffer.append('\n');

        for (var it = children.iterator(); it.hasNext(); ) {
            var next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
