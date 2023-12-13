package de.plixo.atic.hir;

import de.plixo.atic.parsing.Node;
import de.plixo.atic.tir.path.Unit;

/**
 * Simple class for handling all the top level items from a unit
 */
public class HIRUnitParsing {
    public static void parse(Unit unit, Node node) {
        for (var item : node.list("itemList", "item")) {
            unit.addItem(HIRItemParsing.parse(item));
        }
    }
}
