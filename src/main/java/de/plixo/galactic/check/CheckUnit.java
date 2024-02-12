package de.plixo.galactic.check;

import de.plixo.galactic.Universe;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.Import;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.plixo.galactic.exception.FlairKind.*;

public class CheckUnit {

    public void check(Unit unit, CompileRoot root, Universe language, CheckProject checkProject) {
        if (!CheckProject.isAllowedTopLevelName(unit.localName())) {
            throw new FlairException(STR."Unit name \{unit.name()} is not allowed");
        }
        var context = new Context(language, unit, null, root, language.loadedBytecode());
        var names = new HashSet<String>();
        checkMethods(unit.staticMethods(), context, checkProject, names);
        checkImports(unit.imports(), names);
        checkClasses(unit.classes(), context, checkProject, names);
    }

    private void checkClasses(List<StellaClass> classes, Context context, CheckProject checkProject,
                              Set<String> names) {
        for (StellaClass aClass : classes) {
            var classContext =
                    new Context(context.language(), context.unit(), aClass, context.root(),
                            context.language().loadedBytecode());
            classContext.thisContext(aClass);
            if (!names.add(aClass.localName())) {
                throw new FlairCheckException(aClass.region(), NAME,
                        STR."Duplicate name for class \{aClass.localName()}");
            }
            checkProject.checkClass().check(aClass, classContext, checkProject);
        }
    }

    private void checkMethods(List<StellaMethod> methods, Context context,
                              CheckProject checkProject, Set<String> names) {
        for (StellaMethod method : methods) {
            context.thisContext(method.thisContext());
            var name = method.localName();
            names.add(name);
//            if (!) {
            // throw new FlairCheckException(method.region(), NAME,
            //       STR."Duplicate name for method \{name}");
//            }
            if (!CheckProject.isAllowedTopLevelName(name)) {
                throw new FlairCheckException(method.region(), NAME,
                        STR."Method name \{name} is not allowed");
            }
            for (Parameter parameter : method.parameters()) {
                var paramName = parameter.name();
                if (!CheckProject.isAllowedTopLevelName(paramName) && !paramName.equals("this")) {
                    throw new FlairCheckException(method.region(), NAME,
                            STR."Parameter name \{paramName} is not allowed");
                }
                if (Type.isSame(parameter.type(), new VoidType())) {
                    throw new FlairCheckException(method.region(), TYPE_MISMATCH,
                            STR."Parameter type \{parameter.name()} cant be void");
                }
            }
            if (method.body() == null) {
                throw new FlairCheckException(method.region(), NAME,
                        STR."Method \{name} has no body");
            }
            CheckProject.checkMethodBody(context, checkProject, method);
        }
    }


    private void checkImports(List<Import> imports, Set<String> names) {
        for (var anImport : imports) {
            var alias = anImport.alias();
            if (anImport.isUserDefined()) {
                if (!CheckProject.isAllowedTopLevelName(alias)) {
                    throw new FlairCheckException(anImport.region(), NAME,
                            STR."Import name \{alias} is not allowed");
                }
            }
            switch (anImport) {
                case Import.ClassImport(var _, var _, var aclass, var _) -> {
                    if (anImport.isUserDefined() && !names.add(alias)) {
                        throw new FlairCheckException(anImport.region(), IMPORT,
                                STR."Duplicate name for import alias \{anImport.alias()}");
                    }
                    if (!aclass.isPublic()) {
                        throw new FlairCheckException(anImport.region(), IMPORT,
                                STR."Cant import non public class \{aclass.name()}");
                    }
                }
                case Import.StaticMethodImport(var _, var _, var method, var _) -> {
                    if (!method.isStatic()) {
                        throw new FlairCheckException(anImport.region(), IMPORT,
                                STR."Cant import non static method \{method.localName()}");
                    }
                }
            }

        }
    }
}
