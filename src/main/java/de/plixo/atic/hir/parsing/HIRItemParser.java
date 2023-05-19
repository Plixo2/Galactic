package de.plixo.atic.hir.parsing;

import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.hir.item.*;
import de.plixo.atic.hir.parsing.records.Definition;
import de.plixo.atic.hir.parsing.records.VarDefinition;
import de.plixo.atic.hir.parsing.records.WordList;
import de.plixo.atic.lexer.Node;
import de.plixo.atic.hir.parsing.records.WordChain;

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
            throw new LanguageError(topEntry.child().region(), "cant process node " + topEntry.child());
        }
    }

    private static List<HIRAnnotation> parseAnnotationList(Node node) {
        var list = node.list("annotationList", "annotation");
        return list.stream().map(ref -> {
            var paramOpt = ref.get("annotationParamOpt");
            var args = paramOpt.list("expressionList", "expressionListOpt", "expression").stream()
                    .map(HIRExprParser::parse).toList();
            return new HIRAnnotation(ref.getID(),args);
        }).toList();
    }

    private static HIRImport parseImport(Node node, List<HIRAnnotation> annotations) {
        return new HIRImport(WordChain.create(node).words(), node.get("importAll").hasChildren()
                , annotations);
    }

    private static HIRConst parseConstant(Node node, List<HIRAnnotation> annotations) {
        var varDefinition = VarDefinition.create(node.get("varDefinition"));
        return new HIRConst(varDefinition.name(), varDefinition.expression(),
                varDefinition.typehint() , annotations);
    }

    private static HIRStruct parseStruct(Node node, List<HIRAnnotation> annotations) {
        var name = node.getID();
        var generics = WordList.create(node.get("genericDefinition"));

        var definitionNodes = node.get("definitionBlock").list("definitionList", "definition");
        var definitions = definitionNodes.stream().map(Definition::create)
                .map(ref -> new ArgDefinition(ref.name(), ref.typehint(), ref.expression()));

        return new HIRStruct(name, definitions.toList(), generics.words(), annotations);
    }
}
