package de.plixo.atic.hir;

import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRField;
import de.plixo.atic.hir.items.HIRMethod;
import de.plixo.atic.hir.types.HIRClassType;
import de.plixo.atic.hir.types.HIRType;
import de.plixo.atic.parsing.Node;
import de.plixo.atic.tir.ObjectPath;

import java.util.ArrayList;

public class HIRClassParsing {
    public static HIRClass parse(Node node) {
        node.assertType("class");
        var name = node.getID();
        var extendsNode = node.get("extends");
        var implementsNode = node.get("implements");
        HIRType superClass = new HIRClassType(new ObjectPath("java", "lang", "Object"));
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
                methods.add(parseMethod(item.get("method")));
            } else if (item.has("field")) {
                fields.add(parseField(item.get("field")));
            }
        }

        return new HIRClass(name, superClass, implementsList, fields, methods);
    }

    private static HIRMethod parseMethod(Node node) {
        node.assertType("method");
        var name = node.getID();
        var parameters = node.list("parameterList", "parameter");
        var returnType = HIRTypeParsing.parse(node.get("type"));
        var blockExpr = HIRExpressionParsing.parse(node.get("expression"));
        var parameterList = parameters.stream().map(param -> {
            var paramID = param.getID();
            var type = HIRTypeParsing.parse(param.get("type"));
            return new HIRMethod.Parameter(paramID, type);
        }).toList();

        return new HIRMethod(name, parameterList, returnType, blockExpr);
    }

    private static HIRField parseField(Node node) {
        node.assertType("field");
        var name = node.getID();
        var type = HIRTypeParsing.parse(node.get("type"));
        return new HIRField(name, type);
    }
}
