package de.plixo.atic;

import com.sun.source.tree.Tree;
import de.plixo.atic.common.JsonUtil;
import de.plixo.atic.exceptions.reasons.FileIOFailure;
import de.plixo.atic.exceptions.reasons.ThreadFailure;
import de.plixo.atic.files.FileTree;
import de.plixo.atic.files.PathEntity;
import de.plixo.atic.hir.item.HIRItem;
import de.plixo.atic.hir.parsing.HIRItemParser;
import de.plixo.atic.tir.building.TreeBuilder;
import de.plixo.atic.tir.building.UnitBuilder;
import de.plixo.atic.tir.tree.Import;
import de.plixo.atic.tir.tree.Package;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.v2.hir2.HIRItemParsing;
import de.plixo.atic.v2.hir2.items.HIRBlock;
import de.plixo.atic.v2.hir2.items.HIRFunction;
import de.plixo.atic.v2.tir.CompileRoot;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.Method;
import de.plixo.atic.v2.tir.TreeBuilding;
import de.plixo.atic.v2.tir.parsing.TIRExpressionParsing;
import de.plixo.atic.v2.tir.parsing.TIRFunctionParsing;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@AllArgsConstructor

public class Language extends Object {
    public static int TASK_CREATED = 0;

    private final ParseConfig config;


    public void parse(File file) {
        var time = System.currentTimeMillis();
        removeDebugFiles("resources/out/");

        var lex = readAndLex(file);
        var hir2 = buildHIR2(lex);
        debugHIR(hir2);
        var tree2 = buildTree2(hir2);
        TIR(tree2, hir2);


        //    var hir = buildHIR(lex);

        //  debugHIR(hir);
//
        //  var tree = buildTree(hir);
//
        //  addImports(tree);
        //  addConstants(tree);
        //  addAnnotations(tree);
        //  addFields(tree);
        //  addDefaultsAndExpression(tree);
//
//
//
        //  debugUnits(tree.units());
        System.out.println("created " + TASK_CREATED + " tasks");
        var currentTime = System.currentTimeMillis();
        System.out.println("Took " + (currentTime - time) + " ms");

    }

    private void TIR(Tree2 tree2, HIR2 hir2) {
        var units = tree2.root().listUnits();
        for (var unit : units) {
            var pathUnit = unit.pathUnit();
            var hirItems = Objects.requireNonNull(hir2.mapping.get(pathUnit));
            for (var hirItem : hirItems) {
                if (hirItem instanceof HIRFunction function) {
                    TIRFunctionParsing.addFunctionShell(unit,function);
                }
            }
        }
        for (var unit : units) {
            var context = new Context(null,unit);
            for (var method : unit.methods()) {
                TIRFunctionParsing.addFunctionParameters(method,context);
            }
        }
        for (var unit : units) {
            var context = new Context(null,unit);
            for (var method : unit.methods()) {
                TIRFunctionParsing.addFunctionBody(method,context);
            }
        }

        for (var unit : units) {
            var pathUnit = unit.pathUnit();
            var hirItems = Objects.requireNonNull(hir2.mapping.get(pathUnit));
            for (var hirItem : hirItems) {
                if (hirItem instanceof HIRBlock block) {
                    var context = new Context(null,unit);
                    for (var expression : block.expressions()) {
                        var parse = TIRExpressionParsing.parse(expression, context);
                        System.out.println("parse = " + parse);
                        System.out.println("with type of " + parse.asAticType());
                    }
                }
                //        unit.items().add(TIRItemParsing.parse(hirItem));
            }
        }
    }


    private ReadAndLex readAndLex(File file) {
        var fileTree = new FileTree(file, config);
        var projectPath = fileTree.toPath();
        return new ReadAndLex(fileTree, projectPath);
    }

    private HIR2 buildHIR2(ReadAndLex lex) {
        var pathUnits = lex.projectPath().listUnits();
        var mapping = new HashMap<PathEntity.PathUnit, List<de.plixo.atic.v2.hir2.items.HIRItem>>();
        for (var pathUnit : pathUnits) {
            var items = pathUnit.node().list("itemList", "item");
            if (config.threaded()) {
                try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    var tasks = new ArrayList<Future<de.plixo.atic.v2.hir2.items.HIRItem>>();
                    for (var item : items) {
                        TASK_CREATED += 1;
                        tasks.add(executor.submit(() -> HIRItemParsing.parse(item)));
                    }
                    var list = new ArrayList<de.plixo.atic.v2.hir2.items.HIRItem>();
                    for (var task : tasks) {
                        try {
                            list.add(task.get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new ThreadFailure(e).create();
                        }
                    }
                    mapping.put(pathUnit, list);
                }
            } else {
                var list = items.stream().map(HIRItemParsing::parse).toList();
                mapping.put(pathUnit, list);
            }
        }
        return new HIR2(lex.projectPath(), mapping);
    }

    public record HIR2(PathEntity projectPath,
                       Map<PathEntity.PathUnit, List<de.plixo.atic.v2.hir2.items.HIRItem>> mapping) {

    }

    private Tree2 buildTree2(HIR2 hir2) {
        var tree = TreeBuilding.toTree(hir2);
        return new Tree2(tree);
    }


    private record Tree2(CompileRoot root) {

    }

    private static void debugHIR(HIR2 hir) {
        hir.mapping.forEach((pathUnit, hirItem) -> {
            hirItem.forEach(ref -> {
                var pathname = "resources/out/items/" +
                        FilenameUtils.removeExtension(pathUnit.file().toString())
                                .replace("\\", ".") + "." + ref.name() + ".json";
                var debugFile = new File(pathname);
                JsonUtil.saveJsonObj(debugFile, ref.toJson());
            });
        });
    }

    private void addDefaultsAndExpression(Tree tree) {
        Consumer<Unit> mainStatement = (unit) -> {
            UnitBuilder.addDefaults(unit);
            UnitBuilder.addConstantExpressions(unit);
        };


        if (config.threaded()) {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var tasks = new ArrayList<Future<Boolean>>();
                tree.units().forEach(ref -> {
                    TASK_CREATED += 1;
                    tasks.add(executor.submit(() -> {
                        mainStatement.accept(ref);
                        return true;
                    }));
                });
                for (var task : tasks) {
                    try {
                        task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new ThreadFailure(e).create();
                    }
                }
            }
        } else {
            tree.units().forEach(mainStatement);
        }
    }

    private void addFields(Tree tree) {
        tree.units().forEach(UnitBuilder::addFields);
    }

    private void addAnnotations(Tree tree) {
        tree.units().forEach(UnitBuilder::addAnnotations);
    }

    private void addImports(Tree tree) {
        tree.units().forEach(ref -> UnitBuilder.addImports(ref, tree.root()));
        tree.units().forEach(ref -> {
            ref.structs().forEach(c -> ref.addImport(new Import.StructureImport(c)));
        });
    }

    private void addConstants(Tree tree) {
        tree.units().forEach(UnitBuilder::addConstants);
        tree.units().forEach(ref -> {
            ref.constants().forEach(c -> ref.addImport(new Import.ConstantImport(c)));
        });
    }


    private Tree buildTree(HIR hir) {
        var root = TreeBuilder.build(hir.projectPath(), hir.mapping());
        var units = root.flatUnits();
        return new Tree(hir.projectPath(), root, units);
    }


    private HIR buildHIR(ReadAndLex readAndLex) {
        var mapping = new HashMap<PathEntity.PathUnit, List<HIRItem>>();
        var pathUnits = readAndLex.projectPath().listUnits();
        for (var unit : pathUnits) {
            var items = unit.node().list("itemList", "item");
            if (config.threaded()) {
                try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    var tasks = new ArrayList<Future<HIRItem>>();
                    for (var item : items) {
                        TASK_CREATED += 1;
                        tasks.add(executor.submit(() -> HIRItemParser.parse(item)));
                    }
                    var list = new ArrayList<HIRItem>();
                    for (var task : tasks) {
                        try {
                            list.add(task.get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new ThreadFailure(e).create();
                        }
                    }
                    mapping.put(unit, list);
                }
            } else {
                var list = items.stream().map(HIRItemParser::parse).toList();
                mapping.put(unit, list);
            }
        }
        return new HIR(readAndLex.projectPath(), mapping);
    }


    private record ReadAndLex(FileTree fileTree, PathEntity projectPath) {
    }

    private record HIR(PathEntity projectPath,
                       HashMap<PathEntity.PathUnit, List<HIRItem>> mapping) {
    }

    private record Tree(PathEntity projectPath, Package root, List<Unit> units) {
    }


    private static void debugUnits(List<Unit> units) {
        units.forEach(ref -> {
            ref.constants().forEach(con -> {
                var pathname = "resources/out/constants/" + con.absolutName() + ".json";
                var debugFile = new File(pathname);
                assert con.getExpr() != null;
                JsonUtil.saveJsonObj(debugFile, con.getExpr().toJson());
            });
        });
    }

    private static void removeDebugFiles(String pathname) {
        var dump = new File(pathname);
        try {
            FileUtils.deleteDirectory(dump);
        } catch (IOException e) {
            throw new FileIOFailure(dump, FileIOFailure.FileType.TEMP_FILES).create();
        }
    }

}
