package de.plixo.galactic.tir.parsing;

import de.plixo.galactic.Language;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.tir.TypeContext;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.tir.stellaclass.Parameter;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Field;

import static de.plixo.galactic.tir.Scope.INPUT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * different stages used in compiling a class
 */
public class TIRClassParsing {


    public static void fillSuperclasses(StellaClass stellaClass, Context context) {
        var hirClass = stellaClass.hirClass();
        var hirType = hirClass.superClass();
        var superClass = TIRTypeParsing.parse(hirType, context);
        if (!(superClass instanceof Class aClass)) {
            throw new NullPointerException("not a class");
        }
        var interfaces = hirClass.interfaces().stream().map(ref -> {
            var parse = TIRTypeParsing.parse(ref, context);
            if (!(parse instanceof Class interfaceClass)) {
                throw new NullPointerException("not a class");
            }
            return interfaceClass;
        }).toList();
        stellaClass.superClass = aClass;
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
                    new StellaMethod(ACC_PUBLIC, method.methodName(), parameters, returnType, method,
                            new MethodOwner.ClassOwner(stellaClass));
            stellaClass.addMethod(aticMethod, context);
        }
    }

    public static void fillMethodExpressions(StellaClass stellaClass, TypeContext context,
                                             Language language) {
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
