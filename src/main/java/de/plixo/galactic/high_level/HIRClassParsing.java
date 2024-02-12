package de.plixo.galactic.high_level;

import de.plixo.galactic.high_level.items.HIRClass;
import de.plixo.galactic.high_level.items.HIRField;
import de.plixo.galactic.high_level.items.HIRMethod;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.parsing.Node;

import java.util.ArrayList;


/**
 * Parses a class node from the CFG into a HIRClass
 */
public class HIRClassParsing {
    public static HIRClass parse(Node node) {
        node.assertType("class");
        var name = node.getID();
        var extendsNode = node.get("extends");
        var implementsNode = node.get("implements");
        HIRType superClass = null;
        if (extendsNode.has("type")) {
            superClass = HIRTypeParsing.parse(extendsNode.get("type"));
        }
        var implementsList = new ArrayList<HIRType>();

        if (implementsNode.has("typeList")) {
            var types = implementsNode.list("typeList", "typeListOpt", "type").stream()
                    .map(HIRTypeParsing::parse).toList();
            implementsList.addAll(types);
        }
        var fields = new ArrayList<HIRField>();
        var methods = new ArrayList<HIRMethod>();
        var classBlock = node.get("classBlock");
        var items = classBlock.list("classItemList", "classItem");
        for (var item : items) {
            if (item.has("method")) {
                methods.add(HIRMethodParsing.parse(item.get("method")));
            } else if (item.has("field")) {
                fields.add(parseField(item.get("field")));
            }
        }

        return new HIRClass(node.getIDRegion(), name, superClass, implementsList, fields, methods);
    }

    private static HIRField parseField(Node node) {
        node.assertType("field");
        var name = node.getID();
        var type = HIRTypeParsing.parse(node.get("type"));
        return new HIRField(name, type);
    }
}
