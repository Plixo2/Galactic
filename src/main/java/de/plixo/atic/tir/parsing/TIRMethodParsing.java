package de.plixo.atic.tir.parsing;

import de.plixo.atic.Language;
import de.plixo.atic.tir.TypeContext;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;

public class TIRMethodParsing {

    public static void parse(AticMethod ref, TypeContext context, Language language) {
        if (ref.hirMethod() != null) {
            var expression = ref.hirMethod().expression();
            ref.body = TIRExpressionParsing.parse(expression, context);
            ref.body = language.symbolsStage().parse(ref.body, context);
            ref.body = language.inferStage().parse(ref.body, context);
            language.checkStage().parse(ref.body, context);
            var expected = ref.returnType();
            assert ref.body != null;

            var found = ref.body.getType(context);
            var isVoid = Type.isSame(expected, new VoidType());
            var typeMatch = Type.isAssignableFrom(expected, found, context);
            if (!typeMatch && !isVoid) {
                throw new NullPointerException(
                        "method return type doesnt match, expected " + expected + ", but found " +
                                found);
            }
        }
    }
}
