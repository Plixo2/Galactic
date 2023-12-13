package de.plixo.atic.hir;

import de.plixo.atic.hir.items.HIRItem;
import de.plixo.atic.parsing.Node;
import de.plixo.atic.tir.path.Unit;

import java.util.ArrayList;

public class HIRUnitParsing {
    public static void parse(Unit unit, Node node) {
        var hirItems = new ArrayList<HIRItem>();
        for (var item : node.list("itemList", "item")) {
            hirItems.add(HIRItemParsing.parse(item));
        }
        unit.setHirItems(hirItems);
    }
}
