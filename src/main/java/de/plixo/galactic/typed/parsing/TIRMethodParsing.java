package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public class TIRMethodParsing {

    public static void parse(StellaMethod ref, Context context) {
        var language = context.language();
        if (ref.hirMethod() != null) {
            var expression = ref.hirMethod().expression();
            ref.body = TIRExpressionParsing.parse(expression, context);
            ref.body = language.symbolsStage().parse(ref.body, context, 0);
            ref.body = language.inferStage().parse(ref.body, context, ref.returnType());
            language.checkStage().parse(ref.body, context, 0);
            var expected = ref.returnType();
            assert ref.body != null;

            var found = ref.body.getType(context);
            var isVoid = Type.isSame(expected, new VoidType());
            var typeMatch = Type.isAssignableFrom(expected, found, context);
            if (!typeMatch && !isVoid) {
                throw new NullPointerException(
                        STR."method return type doesnt match, expected \{expected}, but found \{found}");
            }
        }
    }


}
