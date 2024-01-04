package de.plixo.galactic.high_level;

import de.plixo.galactic.high_level.items.HIRMethod;
import de.plixo.galactic.high_level.items.HIRParameter;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.parsing.Node;

public class HIRMethodParsing {
    public static HIRMethod parse(Node node) {
        node.assertType("method");
        var name = node.getID();
        var parameters = node.list("parameterList", "parameterListOpt", "parameter");
        var methodReturnType = node.get("returnTypeOpt");
        HIRType returnType = null;
        if (methodReturnType.has("type")) {
            returnType = HIRTypeParsing.parse(methodReturnType.get("type"));
        }
        var blockExpr = HIRExpressionParsing.parse(node.get("expression"));
        var parameterList = parameters.stream().map(param -> {
            var paramID = param.getID();
            var type = HIRTypeParsing.parse(param.get("type"));
            return new HIRParameter(param.region(), paramID, type);
        }).toList();

        HIRType extendsType = null;
        var anExtends = node.get("extends");
        if (anExtends.has("type")) {
            extendsType = HIRTypeParsing.parse(anExtends.get("type"));
        }

        return new HIRMethod(node.getIDRegion(), name, parameterList, returnType, blockExpr, extendsType);
    }
}
