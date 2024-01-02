package de.plixo.galactic.check;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.typed.stellaclass.method.MethodImplementation;
import de.plixo.galactic.types.*;
import de.plixo.galactic.types.Class;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckClass {

    public void check(StellaClass stellaClass, Context context, CheckProject checkProject) {
        if (!CheckProject.isAllowedTopLevelName(stellaClass.localName())) {
            throw new FlairCheckException(stellaClass.region(), FlairKind.NAME,
                    STR."Class name \{stellaClass.localName()} is not allowed");
        }
        var superClass = stellaClass.getSuperClass();
        if (superClass != null) {
            if (!superClass.isPublic()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Superclass must be public");
            }
            if (superClass.isFinal()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Superclass must not be final");
            }
            if (superClass.isInterface()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Superclass must not be an interface");
            }
        }
        for (Class anInterface : stellaClass.interfaces) {
            if (!anInterface.isPublic()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Interface must be public");
            }
            if (!anInterface.isInterface()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Interface must be an interface");
            }
            if (anInterface.isFinal()) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.SECURITY,
                        "Interface must not be final");
            }
        }
        var set = checkMethods(stellaClass.methods(), context, checkProject);
        for (Field field : stellaClass.fields) {
            var name = field.name();
            if (!set.add(name)) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.NAME,
                        STR."Duplicate field or method \{name}");
            }
            if (!CheckProject.isAllowedTopLevelName(name)) {
                throw new FlairCheckException(stellaClass.region(), FlairKind.NAME,
                        STR."Field name \{name} is not allowed");
            }
        }
        var implementationLeft = stellaClass.implementationLeft(context);
        if (!implementationLeft.isEmpty()) {
            throw new FlairCheckException(stellaClass.region(), FlairKind.NAME,
                    STR."Class \{stellaClass.name()} does not implement all methods");
        }
    }

    private Set<String> checkMethods(List<MethodImplementation> methods, Context context,
                                     CheckProject checkProject) {
        var names = new HashSet<String>();
        for (var method : methods) {
            //TODO check modifiers of super method and signature and stuff
            var stellaMethod = method.stellaMethod();
            var name = stellaMethod.localName();
            if (!names.add(name)) {
                throw new FlairCheckException(stellaMethod.region(), FlairKind.NAME,
                        STR."Duplicate method \{name}");
            }
            if (!CheckProject.isAllowedTopLevelName(name) && !stellaMethod.isConstructor()) {
                throw new FlairCheckException(stellaMethod.region(), FlairKind.NAME,
                        STR."Method name \{name} is not allowed");
            }
            if (stellaMethod.body() == null) {
                throw new FlairCheckException(stellaMethod.region(), FlairKind.NAME,
                        STR."Method \{name} has no body");
            }
            CheckProject.checkMethodBody(context, checkProject, stellaMethod);
        }
        return names;
    }
}
