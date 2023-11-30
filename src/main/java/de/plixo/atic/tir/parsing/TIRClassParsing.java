package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.hir.expressions.HIRBlock;
import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.hir.items.HIRTopBlock;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.sub.AField;

import static de.plixo.atic.tir.Scope.INPUT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class TIRClassParsing {


    public static void parse(Unit unit, CompileRoot root, Language language) {
        for (var hirItem : unit.getHirItems()) {
            if (hirItem instanceof HIRClass hirClass) {
                var parsed = TIRClassParsing.parseClass(unit, hirClass);
                unit.addClass(parsed);
            } else if (hirItem instanceof HIRTopBlock block) {
                var hirBlock = new HIRBlock(block.expressions());
                var aticBlock = new AticBlock(unit, hirBlock);
                unit.addBlock(aticBlock);
            }
        }
    }


    public static void parseBlock(Unit unit, CompileRoot root, AticBlock block, Language language) {
        var context = new TypeContext(unit, root);
        var base = TIRExpressionParsing.parse(block.hirBlock(), context);
        base = language.symbolsStage().parse(base, context);
        base = language.inferStage().parse(base, context);
        language.checkStage().parse(base, context);

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
            var aticMethod = new AticMethod(aticClass, ACC_PUBLIC, method.methodName(), parameters,
                    returnType, method);
            aticClass.addMethod(aticMethod, context);
        }
    }

    public static void fillMethodExpressions(AticClass aticClass, TypeContext context,
                                             Language language) {
        for (var method : aticClass.methods()) {
            var aticMethod = method.aticMethod();
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
