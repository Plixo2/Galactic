package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.Universe;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.high_level.expressions.HIRBlock;
import de.plixo.galactic.high_level.items.HIRClass;
import de.plixo.galactic.high_level.items.HIRImport;
import de.plixo.galactic.high_level.items.HIRStaticMethod;
import de.plixo.galactic.high_level.items.HIRTopBlock;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.PathElement;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.*;
import de.plixo.galactic.types.Class;

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
                        new StellaClass(hirClass.region(), hirClass.className(), unit, hirClass, defaultSuperClass);
                unit.addClass(parsed);
            } else if (hirItem instanceof HIRTopBlock block) {
                var hirBlock = new HIRBlock(block.region(), block.expressions());
                var stellaBlock = new StellaBlock(unit, hirBlock);
                unit.addBlock(stellaBlock);
            } else if (!(hirItem instanceof HIRImport || hirItem instanceof HIRStaticMethod)) {
                throw new NullPointerException(STR."unknown hir item \{hirItem}");
            }
        }
    }

    /**
     * parses High level imports to imports. This method will get run twice, once for types, once for static methods
     *
     * @param unit     the unit to add the imports to
     * @param root     the root of the project, to start the search
     * @param bytecode the loaded bytecode, to locate java imports
     */
    public static void parseImports(Unit unit, CompileRoot root, LoadedBytecode bytecode,
                                    boolean throwErrors) {
        // clear imports, from previous run
        unit.clearImports();
        unit.classes().forEach(ref -> {
            unit.addImport(ref.localName(), ref);
        });
        unit.staticMethods().forEach(ref -> {
            unit.addImport(ref.localName(), ref);
        });

        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRImport hirImport) {
                var path = hirImport.path();
                var importType = hirImport.importType();
                var alias = hirImport.name();
                if (importType == null) {
                    var located = root.toPathElement().get(path);
                    switch (located) {
                        case PathElement.StellaClassElement(var stellaClass) -> {
                            if (alias.equals("*")) {
                                if (throwErrors) {
                                    throw new FlairCheckException(hirImport.region(),
                                            FlairKind.IMPORT,
                                            STR."Cant import inner parts of a class \{stellaClass.name()}");
                                }
                            }
                            unit.addImport(alias, stellaClass);
                        }
                        case PathElement.UnitElement(var unitElement) -> {
                            if (alias.equals("*")) {
                                unitElement.classes().forEach(ref -> {
                                    unit.addImport(ref.localName(), ref);
                                });
                                unitElement.staticMethods().forEach(ref -> {
                                    unit.addImport(ref.localName(), ref);
                                });
                            } else {
                                if (throwErrors) {
                                    throw new FlairCheckException(hirImport.region(),
                                            FlairKind.IMPORT,
                                            STR."could not locate matching stella class \{path}");
                                }
                            }
                        }
                        case PathElement.StellaMethodElement(var method) -> {
                            if (alias.equals("*")) {
                                if (throwErrors) {
                                    throw new FlairCheckException(hirImport.region(),
                                            FlairKind.IMPORT,
                                            STR."Cant import inner parts of a method \{method.localName()}");
                                }
                            }
                            unit.addImport(alias, method);
                        }
                        case null, default -> {
                            if (throwErrors) {
                                throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                        STR."could not locate matching stella class, or method \{path}");
                            }
                        }
                    }

                } else if (importType.equals("java")) {
                    var jvmClass = unit.locateJVMClass(path, bytecode);
                    if (alias.equals("*")) {
                        if (throwErrors) {
                            throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                    "import * not supported on java");
                        }
                    }
                    if (jvmClass == null) {
                        if (throwErrors) {
                            throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                    STR."could not locate jvm class \{path}");
                        }
                    }
                    unit.addImport(alias, jvmClass);
                } else {
                    if (throwErrors) {
                        throw new FlairCheckException(hirImport.region(), FlairKind.IMPORT,
                                STR."unknown import type \{importType}");
                    }
                }
            }
        }
    }

    public static void fillBlockExpressions(Unit unit, StellaBlock block, Context context) {
        var language = context.language();
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
                var owner = new MethodOwner.UnitOwner(unit);
                var stellaMethod =
                        new StellaMethod(flags, name, parameters, returnType, hirMethod, owner);
                unit.addStaticMethod(stellaMethod);
            }
        }
    }

    public static void fillMethodExpressions(Unit unit, Context context, Universe language) {
        unit.staticMethods().forEach(ref -> {
            context.pushScope();
            ref.parameters().forEach(var -> {
                context.scope().addVariable(var.variable());
            });
            TIRMethodParsing.parse(ref, context);
            context.popScope();
        });
    }
}
