package de.plixo.atic.hir;

import de.plixo.atic.hir.items.*;
import de.plixo.atic.hir.utils.DotWordChain;
import de.plixo.atic.lexer.Node;

public class HIRItemParsing {

    public static HIRItem parse(Node node) {
        node.assertType("item");
        if (node.has("import")) {
            var anImportNode = node.get("import");
            return parseImport(anImportNode);
        } else if (node.has("class")) {
            var aClass = node.get("class");
            return parseClass(aClass);
        } else if (node.has("block")) {
            return parseBlock(node.get("block"));
        } else if (node.has("method")) {
            return parseStaticMethod(node.get("method"));
        }
        throw new NullPointerException("Unknown item");
    }

    private static HIRStaticMethod parseStaticMethod(Node node) {
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

        return new HIRStaticMethod(new HIRMethod(name, parameterList, returnType, blockExpr));
    }

    private static HIRBlock parseBlock(Node node) {
        node.assertType("block");
        var blockExpr = node.get("blockExpr");
        var list = blockExpr.list("blockExprList", "expression").stream()
                .map(HIRExpressionParsing::parse).toList();
        return new HIRBlock(node.region(), list);
    }

    private static HIRClass parseClass(Node node) {
        return HIRClassParsing.parse(node);
    }

    private static HIRImport parseImport(Node node) {
        node.assertType("import");
        var importTypeNode = node.get("importType");
        var dotWordChain = node.get("dotWordChain");
        var wordList = new DotWordChain(dotWordChain);
        String importType = null;
        if (importTypeNode.has("id")) {
            importType = importTypeNode.getID();
        }
        return new HIRImport(dotWordChain.region(), importType, wordList);
    }


}
