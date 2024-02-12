package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.Universe;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.high_level.items.HIRClass;
import de.plixo.galactic.high_level.items.HIRImport;
import de.plixo.galactic.high_level.items.HIRStaticMethod;
import de.plixo.galactic.high_level.items.HIRTopBlock;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.StandardLibs;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.PathElement;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.types.Class;

import java.util.ArrayList;

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
                        new StellaClass(hirClass.region(), hirClass.className(), unit, hirClass,
                                defaultSuperClass);
                unit.addClass(parsed);
            } else if (hirItem instanceof HIRTopBlock block) {
                throw new NullPointerException("TODO remove blocks");
            } else if (!(hirItem instanceof HIRImport || hirItem instanceof HIRStaticMethod)) {
                throw new NullPointerException(STR."unknown hir item \{hirItem}");
            }
        }
    }

    /**
     * parses High level imports to imports. This method will get run twice, once for types,
     * once for static methods, so clearing imports of the unit is necessary
     *
     * @param unit     the unit to add the imports to
     * @param root     the root of the project, to start the search
     * @param bytecode the loaded bytecode, to locate java imports
     */
    public static void parseImports(StandardLibs standardLibs, Unit unit, CompileRoot root,
                                    LoadedBytecode bytecode, boolean throwErrors) {
        // clear imports, from previous run
        unit.clearImports();
        unit.classes().forEach(ref -> {
            unit.addImport(unit.getRegion(), ref.localName(), ref, false);
        });
        for (var anImport : standardLibs.imports()) {
            var path = new ObjectPath(standardLibs.packageName());
            path = path.add(anImport.name());
            addImport(unit, unit.getRegion(), root, "*", path, ImportType.STELLA, bytecode,
                    throwErrors, false);
        }
        unit.staticMethods().forEach(ref -> {
            unit.addImport(unit.getRegion(), ref.localName(), ref, false);
        });

        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRImport hirImport) {
                var region = hirImport.region();
                var path = hirImport.path();
                var importName = hirImport.importType();
                var alias = hirImport.name();
                var importType = switch (importName) {
                    case "java" -> ImportType.JAVA;
                    case null -> ImportType.STELLA;
                    case "stella" -> ImportType.STELLA;
                    default -> {
                        if (throwErrors) {
                            throw new FlairCheckException(region, FlairKind.IMPORT,
                                    STR."unknown import type \{importName}");
                        }
                        yield null;
                    }
                };
                if (importType != null) {
                    addImport(unit, region, root, alias, path, importType, bytecode, throwErrors,
                            true);
                }


            }
        }
    }

    private static void addImport(Unit unit, Region region, CompileRoot root, String alias,
                                  ObjectPath path, ImportType importType, LoadedBytecode bytecode,
                                  boolean throwErrors, boolean userDefined) {
        switch (importType) {
            case STELLA -> {
                var located = root.toPathElement().get(path);
                switch (located) {
                    case PathElement.StellaClassElement(var stellaClass) -> {
                        if (alias.equals("*")) {
                            if (throwErrors) {
                                throw new FlairCheckException(region, FlairKind.IMPORT,
                                        STR."Cant import inner parts of a class \{stellaClass.name()}");
                            }
                        }
                        unit.addImport(region, alias, stellaClass, userDefined);
                    }
                    case PathElement.UnitElement(var unitElement) -> {
                        if (alias.equals("*")) {
                            unitElement.classes().forEach(ref -> {
                                unit.addImport(region, ref.localName(), ref, userDefined);
                            });
                            unitElement.staticMethods().forEach(ref -> {
                                unit.addImport(region, ref.localName(), ref, userDefined);
                            });
                        } else {
                            if (throwErrors) {
                                throw new FlairCheckException(region, FlairKind.IMPORT,
                                        STR."could not locate matching stella class \{path}");
                            }
                        }
                    }
                    case PathElement.StellaMethodElement(var method) -> {
                        if (alias.equals("*")) {
                            if (throwErrors) {
                                throw new FlairCheckException(region, FlairKind.IMPORT,
                                        STR."Cant import inner parts of a method \{method.localName()}");
                            }
                        }
                        unit.addImport(region, alias, method, userDefined);
                    }
                    case null, default -> {
                        if (throwErrors) {
                            throw new FlairCheckException(region, FlairKind.IMPORT,
                                    STR."could not locate matching stella class, or method \{path}");
                        }
                    }
                }
            }
            case JAVA -> {
                var jvmClass = unit.locateJVMClass(path, bytecode);
                if (alias.equals("*")) {
                    if (throwErrors) {
                        throw new FlairCheckException(region, FlairKind.IMPORT,
                                "import * not supported on java");
                    }
                }
                if (jvmClass == null) {
                    if (throwErrors) {
                        throw new FlairCheckException(region, FlairKind.IMPORT,
                                STR."could not locate jvm class \{path}");
                    }
                }
                unit.addImport(region, alias, jvmClass, userDefined);
            }
        }
    }


    public static void fillMethodShells(Unit unit, Context context) {
        for (var method : unit.hirItems()) {
            if (method instanceof HIRStaticMethod staticMethod) {
                var hirMethod = staticMethod.hirMethod();
                var stellaMethod =
                        TIRMethodParsing.parseHIRMethod(hirMethod, ACC_PUBLIC | ACC_STATIC,
                                new MethodOwner.UnitOwner(unit), context);
                var thisType = stellaMethod.extension() != null ? stellaMethod.extension() : null;
                stellaMethod.thisContext(thisType);
                if (thisType != null) {
                    var parameters = new ArrayList<>(stellaMethod.parameters());
                    parameters.addFirst(new Parameter("this", thisType));
                    stellaMethod.parameters(parameters);
                }
                unit.addStaticMethod(stellaMethod);
            }
        }
    }

    public static void fillMethodExpressions(Unit unit, Context context, Universe language) {
        unit.staticMethods().forEach(ref -> {
            context.thisContext(ref.thisContext());
            context.pushScope();
            ref.parameters().forEach(var -> {
                var variable = var.variable();
                if (variable != null) {
                    context.scope().addVariable(variable);
                }
            });
            TIRMethodParsing.parse(ref, context);
            context.popScope();
        });
    }

    private enum ImportType {
        JAVA,
        STELLA
    }
}
