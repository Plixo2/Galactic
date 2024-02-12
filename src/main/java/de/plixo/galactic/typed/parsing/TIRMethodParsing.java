package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.high_level.items.HIRMethod;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public class TIRMethodParsing {

    public static StellaMethod parseHIRMethod(HIRMethod method, int flags, MethodOwner owner,
                                              Context context) {
        var parameters = method.hirParameters().stream().map(ref -> {
            var parse = TIRTypeParsing.parse(ref.type(), context);
            return new Parameter(ref.name(), parse);
        }).toList();

        Type returnType;
        if (method.returnType() != null) {
            returnType = TIRTypeParsing.parse(method.returnType(), context);
        } else {
            returnType = new VoidType();
        }
        Type extensionType = null;
        if (method.extensionType() != null) {
            extensionType = TIRTypeParsing.parse(method.extensionType(), context);
        }

        return new StellaMethod(flags, method.methodName(), parameters, returnType,
                method.expression(), owner, extensionType, method.region());
    }


    public static void parse(StellaMethod ref, Context context) {
        var language = context.language();
        var expression = ref.hirExpression();
        ref.body(TIRExpressionParsing.parse(expression, context));
        ref.body(language.symbolsStage().parse(ref.body(), context, 0));
        ref.body(language.inferStage().parse(ref.body(), context, ref.returnType()));
    }


}
