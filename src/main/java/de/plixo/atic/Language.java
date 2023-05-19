package de.plixo.atic;

import de.plixo.atic.common.JsonUtil;
import de.plixo.atic.exceptions.LanguageError;
import de.plixo.atic.files.FileTree;
import de.plixo.atic.files.PathEntity;
import de.plixo.atic.hir.item.HIRItem;
import de.plixo.atic.hir.parsing.HIRItemParser;
import de.plixo.atic.lexer.Lexer;
import de.plixo.atic.tir.building.TreeBuilder;
import de.plixo.atic.tir.building.UnitBuilder;
import de.plixo.atic.tir.tree.Import;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Language {

    public static int TASK_CREATED = 0;


    public void readProject(File file, ParseConfig config) {
        int runs = 1;
        for (int i = 0; i < runs; i++) {
            TASK_CREATED = 0;
            run(file, config);
        }
        //units.forEach(ref -> System.out.println(ref.imports()));
    }

    private void run(File file, ParseConfig config) {
        try {
            FileUtils.deleteDirectory(new File("resources/out/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var startTimeTotal = System.currentTimeMillis();
        var startTime = System.currentTimeMillis();
        var fileTree = new FileTree(file, config);
        PathEntity projectPath = fileTree.toPath();
        System.out.println("Lexing Total " + (System.currentTimeMillis() - startTime) + "ms");
        var mapping = new HashMap<PathEntity.PathUnit, List<HIRItem>>();
        forEachUnit(projectPath, unit -> {
            LanguageError.errorFile = unit.file();
            var items = unit.node().list("topList", "annotatedItem");
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var tasks = new ArrayList<Future<HIRItem>>();
                for (var item : items) {
                    TASK_CREATED += 1;
                    tasks.add(executor.submit(() -> HIRItemParser.parse(item)));
                }
                var list = new ArrayList<HIRItem>();
                var waitThreadTime = System.currentTimeMillis();
                for (var task : tasks) {
                    try {
                        list.add(task.get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("waited for HIR conversion " +
                        (System.currentTimeMillis() - waitThreadTime) + "ms");
                mapping.put(unit, list);
            }
            items.forEach(ref -> {
                var hirItem = HIRItemParser.parse(ref);
                var pathname = "resources/out/items/" +
                        FilenameUtils.removeExtension(unit.file().toString()).replace("\\", ".") +
                        "." + hirItem.name() + ".json";
                var debugFile = new File(pathname);
                JsonUtil.saveJsonObj(debugFile, hirItem.toJson());
            });
        });
        var root = TreeBuilder.build(projectPath, mapping);
        var units = root.flatUnits();
        startTime = System.currentTimeMillis();
        units.forEach(ref -> UnitBuilder.addImports(ref, root));
        System.out.println("building imports " + (System.currentTimeMillis() - startTime) + "ms");
        units.forEach(ref -> {
            ref.structs().forEach(c -> ref.addImport(new Import.StructureImport(c)));
        });
        startTime = System.currentTimeMillis();
        units.forEach(UnitBuilder::addConstants);
        System.out.println("adding constants " + (System.currentTimeMillis() - startTime) + "ms");
        units.forEach(ref -> {
            ref.constants().forEach(c -> ref.addImport(new Import.ConstantImport(c)));
        });
        startTime = System.currentTimeMillis();
        units.forEach(UnitBuilder::addFields);
        System.out.println("adding fields " + (System.currentTimeMillis() - startTime) + "ms");
        startTime = System.currentTimeMillis();
        boolean multiThreaded = false;

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = new ArrayList<Future<Boolean>>();
            units.forEach(ref -> {
                Callable<Boolean> action = () -> {
                    UnitBuilder.addDefaults(ref);
                    UnitBuilder.addConstantExpressions(ref);
                    return true;
                };
                if (multiThreaded) {
                    TASK_CREATED += 1;
                    tasks.add(executor.submit(action));
                } else {
                    try {
                        action.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            var waitThreadTime = System.currentTimeMillis();
            for (var task : tasks) {
                try {
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(
                    "waited for constant expr " + (System.currentTimeMillis() - waitThreadTime) +
                            "ms");
        }
        System.out.println("building constants " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Waited Total " + (System.currentTimeMillis() - startTimeTotal) + "ms");
        System.out.println("created " + TASK_CREATED + " tasks");

        units.forEach(ref -> {
            ref.constants().forEach(con -> {
                var pathname = "resources/out/constants/" + con.absolutName() + ".json";
                var debugFile = new File(pathname);
                assert con.getExpr() != null;
                JsonUtil.saveJsonObj(debugFile, con.getExpr().toJson());
            });
        });

    }

    private void forEachUnit(PathEntity entity, Consumer<PathEntity.PathUnit> action) {
        if (entity instanceof PathEntity.PathUnit unit) {
            action.accept(unit);
        } else if (entity instanceof PathEntity.PathDir dir) {
            dir.subDirs().forEach(ref -> forEachUnit(ref, action));
            dir.units().forEach(action);
        }
    }


    public record ParseConfig(String filePattern, Lexer lexer) {

    }
}
