package de.plixo.atic.hir.parsing;

import de.plixo.atic.hir.parsing.records.WordChain;
import de.plixo.atic.hir.typedef.HIRClassType;
import de.plixo.atic.hir.typedef.HIRFunctionType;
import de.plixo.atic.hir.typedef.HIRType;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.exceptions.reasons.GrammarNodeFailure;

import java.util.ArrayList;
import java.util.List;

public class HIRTypeParser {

    public static HIRType parse(Node node) {
        if (node.has("classType")) {
            return parseClass(node.get("classType"));
        } else if (node.has("functionType")) {
            return parseFunction(node.get("functionType"));
        } else {
            throw new GrammarNodeFailure("cant parse Node", node).create();
        }
    }

    private static HIRFunctionType parseFunction(Node node) {
        var typeOrTypeList = node.get("typeOrTypeList");
        List<HIRType> typeList;
        HIRType returnType = null;
        if (typeOrTypeList.has("type")) {
            typeList = new ArrayList<>();
            typeList.add(HIRTypeParser.parse(typeOrTypeList.get("type")));
        } else {
            typeList = parseTypeList(typeOrTypeList);
        }
        var returnTypeOpt = node.get("returnTypeOpt");
        var ownerDef = node.get("ownerDef");
        if (returnTypeOpt.has("type")) {
            returnType = HIRTypeParser.parse(returnTypeOpt.get("type"));
        }
        HIRType owner = null;
        if (ownerDef.has("type")) {
            owner = HIRTypeParser.parse(ownerDef.get("type"));
        }
        return new HIRFunctionType(node.region(), typeList, returnType, owner);
    }

    private static HIRClassType parseClass(Node node) {
        var wordChain = WordChain.create(node.get("wordChain"));
        var generics = parseTypeList(node.get("genericHintOpt"));
        return new HIRClassType(node.region(), wordChain.words(), generics);
    }

    private static List<HIRType> parseTypeList(Node node) {
        return node.list("typeList", "typeListOpt", "type").stream().map(HIRTypeParser::parse)
                .toList();
    }
}
