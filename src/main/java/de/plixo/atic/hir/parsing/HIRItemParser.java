package de.plixo.atic.hir.parsing;

import de.plixo.atic.hir.item.*;
import de.plixo.atic.hir.parsing.records.*;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.exceptions.reasons.GrammarNodeFailure;

import java.util.List;

public class HIRItemParser {
    public static HIRItem parse(Node node) {
        var topEntry = node.get("topEntry");
        var hirAnnotations = parseAnnotationList(node);
        if (topEntry.has("import")) {
            return parseImport(topEntry.get("import"),hirAnnotations);
        } else if (topEntry.has("struct")) {
            return parseStruct(topEntry.get("struct"), hirAnnotations);
        } else if (topEntry.has("constant")) {
            return parseConstant(topEntry.get("constant"), hirAnnotations);
        } else {
            throw new GrammarNodeFailure("cant parse Node", node).create();
        }
    }

    private static List<HIRAnnotation> parseAnnotationList(Node node) {
        var list = node.list("annotationList", "annotation");
        return list.stream().map(ref -> {
            var paramOpt = ref.get("annotationParamOpt");
            var args = paramOpt.list("expressionList", "expressionListOpt", "expression").stream()
                    .map(HIRExprParser::parse).toList();
            return new HIRAnnotation(ref.region(),ref.getID(),args);
        }).toList();
    }

    private static HIRImport parseImport(Node node, List<HIRAnnotation> annotations) {
        return new HIRImport(node.region(), WordChain.create(node).words(),
                node.get("importAll").hasChildren()
                , annotations);
    }

    private static HIRConst parseConstant(Node node, List<HIRAnnotation> annotations) {
        var varDefinition = ConstDefinition.create(node.get("constDefine"));
        return new HIRConst(node.region(), varDefinition.name(), varDefinition.expression(),
                varDefinition.typehint() , annotations);
    }

    private static HIRStruct parseStruct(Node node, List<HIRAnnotation> annotations) {
        var name = node.getID();
        var generics = WordList.create(node.get("genericDefinition"));

        var definitionNodes = node.get("definitionBlock").list("definitionList", "annotatedDefinition");
        var definitions = definitionNodes.stream()
                .map(ref -> {
                    var hirAnnotations = HIRItemParser.parseAnnotationList(ref);
                    var def = Definition.create(ref.get("definition"));
                    return new ArgDefinition(def.region(),def.name(), def.typehint(),
                            def.expression(),hirAnnotations);
                });

        return new HIRStruct(node.region(), name, definitions.toList(), generics.words(),
                annotations);
    }
}
