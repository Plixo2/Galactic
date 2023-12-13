package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.hir.expressions.HIRBlock;
import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRImport;
import de.plixo.atic.hir.items.HIRTopBlock;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Unit;

public class TIRUnitParsing {

    public static void parse(Unit unit, CompileRoot root) {
        for (var hirItem : unit.getHirItems()) {
            if (hirItem instanceof HIRClass hirClass) {
                var parsed = new AticClass(hirClass.className(), unit, hirClass);
                unit.addClass(parsed);
            } else if (hirItem instanceof HIRTopBlock block) {
                var hirBlock = new HIRBlock(block.expressions());
                var aticBlock = new AticBlock(unit, hirBlock);
                unit.addBlock(aticBlock);
            } else if (!(hirItem instanceof HIRImport)) {
                throw new NullPointerException("unknown hir item " + hirItem);
            }
        }
    }

    public static void parseImports(Unit unit, CompileRoot root) {
        for (var hirItem : unit.getHirItems()) {
            if (hirItem instanceof HIRImport hirImport) {
                var path = hirImport.path();
                var importType = hirImport.importType();
                if (importType == null) {
                    var types = path.names();
                    var iterator = types.iterator();
                    var located = root;
                    while (iterator.hasNext()) {
                        var name = iterator.next();
                        var next = located.locate(name);
                        if (next == null && iterator.hasNext()) {
                            throw new NullPointerException("could not locate " + name);
                        }
                        if (next instanceof CompileRoot compileRoot) {
                            located = compileRoot;
                        } else if (next instanceof AticClass aticClass) {
                            if (iterator.hasNext()) {
                                throw new NullPointerException("could not locate matching " + name);
                            } else {
                                unit.addImport(hirImport.name(), aticClass);
                            }
                        }
                    }
                } else if (importType.equals("java")) {
                    var jvmClass = unit.locateJVMClass(path);
                    unit.addImport(hirImport.name(), jvmClass);
                } else {
                    throw new NullPointerException("unknown import type " + importType);
                }
            }
        }
    }

    public static void fillBlockExpressions(Unit unit, CompileRoot root, AticBlock block,
                                            Language language) {
        var context = new TypeContext(unit, root);
        var base = TIRExpressionParsing.parse(block.hirBlock(), context);
        base = language.symbolsStage().parse(base, context);
        base = language.inferStage().parse(base, context);
        language.checkStage().parse(base, context);
        block.expression(base);
    }
}
