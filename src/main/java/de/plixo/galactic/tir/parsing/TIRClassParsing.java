package de.plixo.galactic.tir.parsing;

import de.plixo.galactic.Universe;
import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.tir.stellaclass.Parameter;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Field;

import static de.plixo.galactic.tir.Scope.INPUT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * different stages used in compiling a class
 */
public class TIRClassParsing {


    public static void fillSuperclasses(StellaClass stellaClass, Context context,
                                        Class defaultSuperClass) {
        var hirClass = stellaClass.hirClass();
        var hirType = hirClass.superClass();
        Class superClass;
        if (hirType != null) {
            var parsed = TIRTypeParsing.parse(hirType, context);
            if (!(parsed instanceof Class aClass)) {
                throw new FlairCheckException(hirType.region(), FlairKind.UNEXPECTED_TYPE, "not a class");
            }
            superClass = aClass;
        } else {
            superClass = defaultSuperClass;
        }
        var interfaces = hirClass.interfaces().stream().map(ref -> {
            var parse = TIRTypeParsing.parse(ref, context);
            if (!(parse instanceof Class interfaceClass) || !interfaceClass.isInterface()) {
                throw new FlairCheckException(ref.region(), FlairKind.UNEXPECTED_TYPE, "not a interface class");
            }
            return interfaceClass;
        }).toList();
        stellaClass.superClass = superClass;
        stellaClass.interfaces = interfaces;
    }

    public static void fillFields(StellaClass stellaClass, Context context) {
        var hirClass = stellaClass.hirClass();
        for (var field : hirClass.fields()) {
            var name = field.name();
            var type = TIRTypeParsing.parse(field.type(), context);
            var e = new Field(ACC_PUBLIC, name, type, stellaClass);
            stellaClass.fields.add(e);
        }
    }

    public static void fillMethodShells(StellaClass stellaClass, Context context) {
        for (var method : stellaClass.hirClass().methods()) {
            var parameters = method.HIRParameters().stream().map(ref -> {
                var parse = TIRTypeParsing.parse(ref.type(), context);
                return new Parameter(ref.name(), parse);
            }).toList();
            var returnType = TIRTypeParsing.parse(method.returnType(), context);
            var aticMethod =
                    new StellaMethod(ACC_PUBLIC, method.methodName(), parameters, returnType,
                            method, new MethodOwner.ClassOwner(stellaClass));
            stellaClass.addMethod(aticMethod, context);
        }
    }

    public static void fillMethodExpressions(StellaClass stellaClass, TypeContext context,
                                             Universe language) {
        for (var method : stellaClass.methods()) {
            var aticMethod = method.aticMethod();
            context.pushScope();
            aticMethod.parameters().forEach(ref -> {
                context.scope().addVariable(ref.variable());
            });
            context.scope().addVariable(new Scope.Variable("this", INPUT, stellaClass, null));
            TIRMethodParsing.parse(aticMethod, context, language);
            context.popScope();
        }
    }

    public static void assertMethodsImplemented(StellaClass stellaClass) {
        var aMethods = stellaClass.implementationLeft();
        if (!aMethods.isEmpty()) {
            var builder = new StringBuilder();
            aMethods.forEach(ref -> {
                builder.append(ref).append("\n");
            });
            throw new NullPointerException("functions to implement left " + builder);
        }
    }

}
