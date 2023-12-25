package de.plixo.galactic.tir.parsing;

import de.plixo.galactic.Universe;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.hir.expressions.HIRBlock;
import de.plixo.galactic.hir.items.HIRClass;
import de.plixo.galactic.hir.items.HIRImport;
import de.plixo.galactic.hir.items.HIRStaticMethod;
import de.plixo.galactic.hir.items.HIRTopBlock;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.path.PathElement;
import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.tir.stellaclass.Parameter;
import de.plixo.galactic.tir.stellaclass.StellaBlock;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;

import java.util.Objects;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;


/**
 * Functions used for parsing a unit
 */
public class TIRUnitParsing {

    public static void parse(Unit unit, Class defaultSuperClass) {
        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRClass hirClass) {
                var parsed =
                        new StellaClass(hirClass.className(), unit, hirClass, defaultSuperClass);
                unit.addClass(parsed);
            } else if (hirItem instanceof HIRTopBlock block) {
                var hirBlock = new HIRBlock(block.region(), block.expressions());
                var aticBlock = new StellaBlock(unit, hirBlock);
                unit.addBlock(aticBlock);
            } else if (!(hirItem instanceof HIRImport || hirItem instanceof HIRStaticMethod)) {
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
                    var located = root.toPathElement().get(path);
                    if (located instanceof PathElement.StellaClassElement(var aticClass)) {
                        unit.addImport(alias, aticClass);
                    } else if (located instanceof PathElement.UnitElement(var unitElement)) {
                        if (alias.equals("*")) {
                            unitElement.classes().forEach(ref -> {
                                unit.addImport(ref.localName(), ref);
                            });
                        } else {
                            throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                    "could not locate matching atic class " + path);
                        }
                    } else {
                        throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                "could not locate matching atic class " + path);
                    }

                } else if (importType.equals("java")) {
                    var jvmClass = unit.locateJVMClass(path, bytecode);
                    if (alias.equals("*")) {
                        throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                "import * not supported on java");
                    }
                    if (jvmClass == null) {
                        throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                "could not locate jvm class " + path);
                    }
                    unit.addImport(alias, jvmClass);
                } else {
                    throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                            "unknown import type " + importType);
                }
            }
        }
    }

    public static void fillBlockExpressions(Unit unit, CompileRoot root, StellaBlock block,
                                            Universe language) {
        var context = new TypeContext(unit, root, language.loadedBytecode());
        var base = TIRExpressionParsing.parse(block.hirBlock(), context);
        base = language.symbolsStage().parse(base, context);
        base = language.inferStage().parse(base, context);
        language.checkStage().parse(base, context);
        block.expression(base);
    }

    public static void fillMethodShells(Unit unit, Context context) {
        for (var method : unit.hirItems()) {
            if (method instanceof HIRStaticMethod staticMethod) {
                var flags = ACC_PUBLIC | ACC_STATIC;
                var hirMethod = staticMethod.hirMethod();
                var name = hirMethod.methodName();
                var parameters = hirMethod.HIRParameters().stream().map(ref -> {
                    var parse = TIRTypeParsing.parse(ref.type(), context);
                    return new Parameter(ref.name(), parse);
                }).toList();
                var returnType = TIRTypeParsing.parse(hirMethod.returnType(), context);
                var aticMethod =
                        new StellaMethod(flags, name, parameters, returnType, hirMethod, null);
                unit.addStaticMethod(aticMethod);
            }
        }
    }

    public static void fillMethodExpressions(Unit unit, TypeContext context, Universe language) {
        unit.staticMethods().forEach(ref -> {
            context.pushScope();
            ref.parameters().forEach(var -> {
                context.scope().addVariable(var.variable());
            });
            TIRMethodParsing.parse(ref, context, language);
            context.popScope();
        });
    }
}
