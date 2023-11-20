package de.plixo.atic.tir.parsing;

import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRStaticMethod;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.tir.aticclass.method.MethodImplementation;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.sub.AField;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class TIRClassParsing {

    public static void parse(Unit unit) {
        for (var hirItem : unit.hirItems()) {
            if (hirItem instanceof HIRClass hirClass) {
                var parsed = TIRClassParsing.parseClass(unit, hirClass);
                unit.addClass(parsed);
            } else  if (hirItem instanceof HIRStaticMethod hirStaticMethod) {
                throw new NullPointerException("not supported");
                //  var parsed = TIRUnitParsing.parse(unit, hirStaticMethod, );
                //  unit.addClass(parsed);
            }  else {
                throw new NullPointerException("not supported");
            }
        }
    }

    public static AticClass parseClass(Unit unit, HIRClass hirClass) {
        return new AticClass(hirClass.className(), unit, hirClass);
    }

    public static void fillSuperclasses(AticClass aticClass, Context context) {
        var hirClass = aticClass.hirClass();
        var hirType = hirClass.superClass();
        var superClass = TIRTypeParsing.parse(hirType, context);
        if (!(superClass instanceof AClass aClass)) {
            throw new NullPointerException("not a class");
        }
        var interfaces = hirClass.interfaces().stream().map(ref -> {
            var parse = TIRTypeParsing.parse(ref, context);
            if (!(parse instanceof AClass interfaceClass)) {
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
            var e = new AField(ACC_PUBLIC, name, type, aticClass);
            aticClass.fields.add(e);
        }
    }

    public static void fillMethodShells(AticClass aticClass, Context context) {
        for (var method : aticClass.hirClass().methods()) {
            var parameters = method.parameters().stream().map(ref -> new Parameter(ref.name(),
                    TIRTypeParsing.parse(ref.type(), context))).toList();
            var returnType = TIRTypeParsing.parse(method.returnType(), context);
            var aticMethod =
                    new AticMethod(aticClass, ACC_PUBLIC, method.methodName(), parameters, returnType,
                            method);
            aticClass.addMethod(aticMethod, context);
        }
    }

    public static void fillMethodExpressions(AticClass aticClass, Context context) {
        for (MethodImplementation method : aticClass.methods()) {
            var aticMethod = method.aticMethod();
            var hirMethod = aticMethod.hirMethod();
            if (hirMethod != null) {
                aticMethod.body = TIRExpressionParsing.parse(hirMethod.expression(), context);
            }
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
