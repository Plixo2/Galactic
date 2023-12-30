package de.plixo.galactic.high_level;

import de.plixo.galactic.parsing.Node;
import de.plixo.galactic.typed.path.Unit;
import org.jetbrains.annotations.Contract;

/**
 * Simple class for handling all the top level items from a unit
 */
public class HIRUnitParsing {
    @Contract(mutates = "param1")
    public static void parse(Unit unit, Node node) {
        for (var item : node.list("itemList", "item")) {
            unit.addItem(HIRItemParsing.parse(item));
        }
    }
}
