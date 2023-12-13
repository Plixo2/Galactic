package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.hir.expressions.HIRBlock;
import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRImport;
import de.plixo.atic.hir.items.HIRTopBlock;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;

/**
 * Functions used for parsing a unit
 */
public class TIRUnitParsing {

    public static void parse(Unit unit, Class defaultSuperClass) {
        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRClass hirClass) {
                var parsed = new AticClass(hirClass.className(), unit, hirClass, defaultSuperClass);
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

    public static void parseImports(Unit unit, CompileRoot root, LoadedBytecode bytecode) {
        unit.classes().forEach(ref -> {
            unit.addImport(ref.localName(), ref);
        });

        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRImport hirImport) {
                var path = hirImport.path();
                var importType = hirImport.importType();
                var alias = hirImport.name();
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
                            if (!iterator.hasNext()) {
                                if (alias.equals("*") && compileRoot instanceof Unit importedUnit) {
                                    importedUnit.classes().forEach(ref -> {
                                        unit.addImport(ref.localName(), ref);
                                    });
                                } else {
                                    throw new NullPointerException(
                                            "could not locate matching " + name);
                                }
                            }
                        } else if (next instanceof AticClass aticClass) {
                            if (iterator.hasNext()) {
                                throw new NullPointerException("could not locate matching " + name);
                            } else {
                                if (alias.equals("*")) {
                                    throw new NullPointerException(
                                            "import * not supported on " + "class");
                                }
                                unit.addImport(alias, aticClass);
                            }
                        } else {
                            throw new NullPointerException("unknown Path Element type " + next);
                        }
                    }


                } else if (importType.equals("java")) {
                    var jvmClass = unit.locateJVMClass(path, bytecode);
                    if (alias.equals("*")) {
                        throw new NullPointerException("import * not supported on java");
                    }
                    unit.addImport(alias, jvmClass);
                } else {
                    throw new NullPointerException("unknown import type " + importType);
                }
            }
        }
    }

    public static void fillBlockExpressions(Unit unit,
                                            CompileRoot root,
                                            AticBlock block,
                                            Language language) {
        var context = new TypeContext(unit, root, language.loadedBytecode());
        var base = TIRExpressionParsing.parse(block.hirBlock(), context);
        base = language.symbolsStage().parse(base, context);
        base = language.inferStage().parse(base, context);
        language.checkStage().parse(base, context);
        block.expression(base);
    }
}
