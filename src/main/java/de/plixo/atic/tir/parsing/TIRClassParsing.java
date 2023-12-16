package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.Field;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;

import static de.plixo.atic.tir.Scope.INPUT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * different stages used in compiling a class
 */
public class TIRClassParsing {


    public static void fillSuperclasses(AticClass aticClass, Context context) {
        var hirClass = aticClass.hirClass();
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
        aticClass.superClass = aClass;
        aticClass.interfaces = interfaces;
    }

    public static void fillFields(AticClass aticClass, Context context) {
        var hirClass = aticClass.hirClass();
        for (var field : hirClass.fields()) {
            var name = field.name();
            var type = TIRTypeParsing.parse(field.type(), context);
            var e = new Field(ACC_PUBLIC, name, type, aticClass);
            aticClass.fields.add(e);
        }
    }

    public static void fillMethodShells(AticClass aticClass, Context context) {
        for (var method : aticClass.hirClass().methods()) {
            var parameters = method.parameters().stream().map(ref -> {
                var parse = TIRTypeParsing.parse(ref.type(), context);
                return new Parameter(ref.name(), parse);
            }).toList();
            var returnType = TIRTypeParsing.parse(method.returnType(), context);
            var aticMethod =
                    new AticMethod(ACC_PUBLIC, method.methodName(), parameters, returnType, method,aticClass);
            aticClass.addMethod(aticMethod, context);
        }
    }

    public static void fillMethodExpressions(AticClass aticClass, TypeContext context,
                                             Language language) {
        for (var method : aticClass.methods()) {
            var aticMethod = method.aticMethod();
            context.pushScope();
            aticMethod.parameters().forEach(ref -> {
                context.scope()
                        .addVariable(new Scope.Variable(ref.name(), INPUT, ref.type(), null));
            });
            context.scope().addVariable(new Scope.Variable("this", INPUT, aticClass, null));

            var hirMethod = aticMethod.hirMethod();
            if (hirMethod != null) {
                aticMethod.body = TIRExpressionParsing.parse(hirMethod.expression(), context);
                aticMethod.body = language.symbolsStage().parse(aticMethod.body, context);
                aticMethod.body = language.inferStage().parse(aticMethod.body, context);
                language.checkStage().parse(aticMethod.body, context);
                var expected = aticMethod.returnType();
                assert aticMethod.body != null;

                var found = aticMethod.body.getType(context);
                var isVoid = Type.isSame(expected, new VoidType());
                var typeMatch = Type.isAssignableFrom(expected, found, context);
                if (!typeMatch && !isVoid) {
                    throw new NullPointerException(
                            "method return type doesnt match, expected " + expected +
                                    ", but found " + found);
                }
            }
            context.popScope();
        }
    }

    public static void assertMethodsImplemented(AticClass aticClass) {
        var aMethods = aticClass.implementationLeft();
        if (!aMethods.isEmpty()) {
            var builder = new StringBuilder();
            aMethods.forEach(ref -> {
                builder.append(ref).append("\n");
            });
            throw new NullPointerException("functions to implement left " + builder);
        }
    }

}
