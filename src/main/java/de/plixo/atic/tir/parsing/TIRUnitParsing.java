package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.hir.expressions.HIRBlock;
import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRImport;
import de.plixo.atic.hir.items.HIRStaticMethod;
import de.plixo.atic.hir.items.HIRTopBlock;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.PathElement;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;

import java.util.Objects;

import static de.plixo.atic.tir.Scope.INPUT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;


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
                    if (located instanceof PathElement.AticClassElement(var aticClass)) {
                        unit.addImport(alias, aticClass);
                    } else if (located instanceof PathElement.UnitElement(var unitElement)) {
                        if (alias.equals("*")) {
                            unitElement.classes().forEach(ref -> {
                                unit.addImport(ref.localName(), ref);
                            });
                        } else {
                            throw new NullPointerException(
                                    "could not locate matching atic class " + path);
                        }
                    } else {
                        throw new NullPointerException(
                                "could not locate matching atic class " + path);
                    }

                } else if (importType.equals("java")) {
                    var jvmClass = unit.locateJVMClass(path, bytecode);
                    if (alias.equals("*")) {
                        throw new NullPointerException("import * not supported on java");
                    }
                    Objects.requireNonNull(jvmClass, "could not locate " + path);
                    unit.addImport(alias, jvmClass);
                } else {
                    throw new NullPointerException("unknown import type " + importType);
                }
            }
        }
    }

    public static void fillBlockExpressions(Unit unit, CompileRoot root, AticBlock block,
                                            Language language) {
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
                var parameters = hirMethod.parameters().stream().map(ref -> {
                    var parse = TIRTypeParsing.parse(ref.type(), context);
                    return new Parameter(ref.name(), parse);
                }).toList();
                var returnType = TIRTypeParsing.parse(hirMethod.returnType(), context);
                var aticMethod =
                        new AticMethod(flags, name, parameters, returnType, hirMethod, null);
                unit.addStaticMethod(aticMethod);
            }
        }
    }

    public static void fillMethodExpressions(Unit unit, TypeContext context, Language language) {

        unit.staticMethods().forEach(ref -> {
            context.pushScope();
            ref.parameters().forEach(var -> {
                var variable = new Scope.Variable(var.name(), INPUT, var.type(), null);
                var.variable(variable);
                context.scope().addVariable(variable);
            });
            TIRMethodParsing.parse(ref, context, language);
            context.popScope();
        });
    }
}
