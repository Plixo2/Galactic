package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Type;
import de.plixo.galactic.types.VoidType;

public class TIRMethodParsing {

    public static void parse(StellaMethod ref, Context context) {
        var language = context.language();
        var expression = ref.hirExpression();
        ref.body = TIRExpressionParsing.parse(expression, context);
        ref.body = language.symbolsStage().parse(ref.body, context);
        ref.body = language.inferStage().parse(ref.body, context);
    }


}
