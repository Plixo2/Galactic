package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Field;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * different stages used in compiling a class
 */
public class TIRClassParsing {


    public static void fillSuperclasses(StellaClass stellaClass, Context context,
                                        Class defaultSuperClass) {
        var hirClass = stellaClass.hirClass();
        if (hirClass == null) {
            throw new NullPointerException("no hir class, this should not happen in this stage");
        }
        var hirType = hirClass.superClass();
        Class superClass;
        if (hirType != null) {
            var parsed = TIRTypeParsing.parse(hirType, context);
            if (!(parsed instanceof Class aClass)) {
                throw new FlairCheckException(hirType.region(), FlairKind.UNEXPECTED_TYPE,
                        "not a class");
            }
            superClass = aClass;
        } else {
            superClass = defaultSuperClass;
        }
        var interfaces = hirClass.interfaces().stream().map(ref -> {
            var parse = TIRTypeParsing.parse(ref, context);
            if (!(parse instanceof Class interfaceClass) || !interfaceClass.isInterface()) {
                throw new FlairCheckException(ref.region(), FlairKind.UNEXPECTED_TYPE,
                        "not a interface class");
            }
            return interfaceClass;
        }).toList();
        stellaClass.superClass = superClass;
        stellaClass.interfaces = interfaces;
    }

    public static void fillFields(StellaClass stellaClass, Context context) {
        var hirClass = stellaClass.hirClass();
        if (hirClass == null) {
            throw new NullPointerException("no hir class, this should not happen in this stage");
        }
        for (var hirField : hirClass.fields()) {
            var name = hirField.name();
            var type = TIRTypeParsing.parse(hirField.type(), context);
            var field = new Field(ACC_PUBLIC, name, type, stellaClass);
            stellaClass.fields.add(field);
        }
    }

    public static void fillMethodShells(StellaClass stellaClass, Context context) {
        var hirClass = stellaClass.hirClass();
        if (hirClass == null) {
            throw new NullPointerException("no hir class, this should not happen in this stage");
        }
        for (var method : hirClass.methods()) {
            var stellaMethod = TIRMethodParsing.parseHIRMethod(method, ACC_PUBLIC,
                    new MethodOwner.ClassOwner(stellaClass), context);
            stellaMethod.thisContext(stellaClass);
            stellaClass.addMethod(stellaMethod, context);
        }
    }

    public static void fillMethodExpressions(StellaClass stellaClass, Context context) {
        for (var method : stellaClass.methods()) {
            context.thisContext(method.stellaMethod().thisContext());
            var stellaMethod = method.stellaMethod();
            context.pushScope();
            stellaMethod.parameters().forEach(ref -> {
                context.scope().addVariable(ref.variable());
            });
            TIRMethodParsing.parse(stellaMethod, context);
            context.popScope();
        }
    }

    public static void assertMethodsImplemented(StellaClass stellaClass, Context context) {
        var aMethods = stellaClass.implementationLeft(context);
        if (!aMethods.isEmpty()) {
            var builder = new StringBuilder();
            aMethods.forEach(ref -> {
                builder.append(ref).append("\n");
            });
            var msg = STR."functions to implement left: \{builder}";
            throw new FlairCheckException(stellaClass.region(), FlairKind.SIGNATURE, msg);
        }
    }

}
