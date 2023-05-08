package de.plixo.atic;

import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.files.FileTree;
import de.plixo.atic.lexer.Lexer;
import de.plixo.atic.path.PathDir;
import de.plixo.atic.path.PathEntity;
import de.plixo.atic.path.PathUnit;
import de.plixo.common.JsonUtil;
import de.plixo.hir.item.HIRItem;
import de.plixo.hir.parsing.HIRItemParser;
import de.plixo.tir.building.TreeBuilder;
import de.plixo.tir.building.UnitBuilder;
import de.plixo.tir.tree.Import;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Language {

    PathEntity projectPath;

    public void readProject(File file, ParseConfig config) {
        var startTime = System.currentTimeMillis();
        var fileTree = new FileTree(file, config);
        projectPath = fileTree.toPath();
        System.out.println("Waited Total " + (System.currentTimeMillis() - startTime) + "ms");
        var mapping = new HashMap<PathUnit, List<HIRItem>>();
        forEachUnit(projectPath, unit -> {
            LanguageError.errorFile = unit.file;
            var items = unit.node.list("topList", "annotatedItem");
            mapping.put(unit,items.stream().map(HIRItemParser::parse).toList());
            items.forEach(ref -> {
                var hirItem = HIRItemParser.parse(ref);
                var pathname =
                        "resources/out/" + FilenameUtils.removeExtension(unit.file.toString()) +
                                ".item_" + hirItem.name() + ".json";
                var debugFile = new File(pathname);
                JsonUtil.saveJsonObj(debugFile, hirItem.toJson());
            });
        });
        var root = TreeBuilder.build(projectPath, mapping);
        var units = root.flatUnits();
        units.forEach(UnitBuilder::addConstants);
        units.forEach(ref -> UnitBuilder.addImports(ref, root));
        units.forEach(ref -> {
//            new Import.UnitImport(ref)
            ref.constants().forEach(c -> ref.addImport(new Import.ConstantImport(c)));
            ref.structs().forEach(c -> ref.addImport(new Import.StructureImport(c)));
        });
        units.forEach(UnitBuilder::addFields);
        units.forEach(UnitBuilder::addConstantExpressions);
        units.forEach(ref -> System.out.println(ref.imports()));



    }

    private void forEachUnit(PathEntity entity, Consumer<PathUnit> action) {
        if (entity instanceof PathUnit unit) {
            action.accept(unit);
        } else if (entity instanceof PathDir dir) {
            dir.subDirs().forEach(ref -> forEachUnit(ref, action));
            dir.units().forEach(action);
        }
    }


    public record ParseConfig(String filePattern, Lexer lexer) {

    }
}
